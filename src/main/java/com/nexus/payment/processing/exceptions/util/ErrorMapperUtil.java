package com.nexus.payment.processing.exceptions.util;

import com.nexus.payment.processing.dto.Notification;
import com.nexus.payment.processing.exceptions.ErrorCode;
import com.nexus.payment.processing.exceptions.ErrorMetaData;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@EnableConfigurationProperties
@Service
// @Data
public class ErrorMapperUtil {

  public static final int DOWNSTREAM_ERROR_CATEGORY = 500;
  @Getter @Setter private static List<ErrorMetaData> errorMetadataList;

  public static ErrorMetaData getErrorMetadataList(ErrorCode code, ErrorMetaData.Source source) {

    return errorMetadataList.stream()
        .filter(emd -> emd.getCode().equals(code) && emd.getSource().equals(source))
        .findAny()
        .orElse(genericError());
  }

  private static ErrorMetaData genericError() {
    return ErrorMetaData.builder()
        .category(DOWNSTREAM_ERROR_CATEGORY)
        .message("Downstream error")
        .severity(ErrorMetaData.Severity.MEDIUM)
        .code(ErrorCode.E0001)
        .source(ErrorMetaData.Source.SOURCE1)
        .build();
  }

  public static List<Notification> remapToNotifications(Set<ErrorMetaData> errorMetaData) {

    List<ErrorMetaData> thrownErrorMetadataList =
        errorMetaData.stream()
            .map(emd -> getErrorMetadataList(emd.getCode(), emd.getSource()))
            .collect(Collectors.toList());

    return thrownErrorMetadataList.stream().map(getNotification()).collect(Collectors.toList());
  }

  private static Function<ErrorMetaData, Notification> getNotification() {
    return emd ->
        Notification.builder()
            .category(emd.getCategory())
            .code(emd.getCode().name())
            .message(emd.getMessage())
            .severity(emd.getSeverity().name())
            .source(emd.getSource().name())
            .type(emd.getType().name())
            .build();
  }

  public static HttpStatus getHttpStatus(Set<ErrorMetaData> errorMetaData) {
    return errorMetaData.stream()
        .map(emd -> HttpStatus.valueOf(emd.getCategory()))
        .findAny()
        .orElse(HttpStatus.INTERNAL_SERVER_ERROR);
  }

  //    @Data
  @Component
  @ConfigurationProperties(prefix = "error-mappings")
  public class ErrorMapping {

    @Getter @Setter private List<ErrorMetaData> errorMetadataList;

    @PostConstruct
    private void init() {
      ErrorMapperUtil.errorMetadataList = Collections.unmodifiableList(errorMetadataList);
    }
  }
}
