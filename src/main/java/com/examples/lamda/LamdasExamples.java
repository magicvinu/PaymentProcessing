package com.examples.lamda;

import static java.util.Arrays.asList;
import static java.util.Comparator.comparingInt;
import static java.util.Map.entry;
import static java.util.stream.Collectors.*;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class LamdasExamples {

  public static void main(String[] args) {

    LamdasExamples lamdasExamples = new LamdasExamples();
    //        menu ===> [pork, beef, chicken, french fries, rice, season fruit, pizza, prawns,
    // Water, salmon]
    lamdasExamples.shortMenu();
    lamdasExamples.groupByVegetarian();
    lamdasExamples.calorieLevelByType();
    lamdasExamples.groupByDishTypeAndVegetarianAndCalorieType();
    lamdasExamples.sumInDifferentWays();
    lamdasExamples.totalCroupByDishType();
    lamdasExamples.groupByDishType();
    //        lamdasExamples.populateDishTagsGroupByDishTypeUsingJava9();
    lamdasExamples.groupByDishTypeWithName();
    lamdasExamples.groupIsVegetarianAndByDishType();
    lamdasExamples.groupByDishTypeAndIsVegetarian();
    lamdasExamples.findNumberOfDishesGroupByDishType();
    lamdasExamples.groupByCaloriesType();
    lamdasExamples.sortDishComparingUsingCaloriesAndThenComparingByName();
    lamdasExamples.findDishWithHighestCalories();
    lamdasExamples.findDishWithHighestCaloriesForEachDishType();
    lamdasExamples.averageOrSummarizingCalories();
    lamdasExamples.noOfDishesUsingMapAndReduce();
    lamdasExamples.countingNumberOfDish();
    //        lamdasExamples.groupDishesWithDishTypeWithMoreThan500CaloriesUsingJava9();
    lamdasExamples.findDishWithHighestCaloriesPartitionByVegetarian();
    lamdasExamples.countByVegetarian();
    lamdasExamples.primeNumbers();
    lamdasExamples.fibonacciSequence();
    lamdasExamples.collectionFactoryExamples();
    lamdasExamples.usingMapGetDefaultMethod();
  }

  private void shortMenu() {
    System.out.println(
        "shortMenu option 1 ==> "
            + menu.stream().map(Dish::getName).collect(Collectors.joining(", ")));
    System.out.println(
        "shortMenu option 2 ==> "
            + menu.stream()
                .map(Dish::getName)
                .collect(Collectors.reducing((s1, s2) -> s1 + ", " + s2))
                .get());
    //        shortMenu option 1 ==> pork, beef, chicken, french fries, rice, season fruit, pizza,
    // prawns, Water, salmon
    //        shortMenu option 2 ==> pork, beef, chicken, french fries, rice, season fruit, pizza,
    // prawns, Water, salmon
  }

  private void groupByDishTypeWithName() {
    Map<Dish.Type, List<String>> groupByDishTypeWithName =
        menu.stream()
            .collect(
                Collectors.groupingBy(Dish::getType, Collectors.mapping(Dish::getName, toList())));
    System.out.println("groupByDishTypeWithName ==> " + groupByDishTypeWithName);
    //        groupByDishTypeWithName ==> {FISH=[prawns, salmon], OTHER=[french fries, rice, season
    // fruit, pizza, Water], MEAT=[pork, beef, chicken]}
  }

  //    private void populateDishTagsGroupByDishTypeUsingJava9() {
  //
  //        Map<Dish.Type, List<Dish>> groupMenuBasedOnDishType =
  // menu.stream().collect(groupingBy(Dish::getType));
  //        System.out.println("groupMenuBasedOnDishType ==> " + groupMenuBasedOnDishType);
  //        // groupMenuBasedOnDishType ==> {FISH=[prawns, salmon], MEAT=[pork, beef, chicken],
  // OTHER=[french fries, rice, season fruit, pizza, Water]}
  //
  //        Map<Dish.Type, Set<Object>>  groupMenuBasedOnDishTypeMappedToDishTags =
  // menu.stream().collect(groupingBy(Dish::getType,
  //        mapping(dish -> dishTags.get( dish.getName() ),
  //        toSet())));
  //        System.out.println("groupMenuBasedOnDishTypeMappedToDishTags ==> " +
  // groupMenuBasedOnDishTypeMappedToDishTags);
  //        // groupMenuBasedOnDishTypeMappedToDishTags ==>
  //        // {FISH=[[tasty, roasted], [delicious, fresh]], MEAT=[[fried, crisp], [salty, roasted],
  // [greasy, salty]],
  //        // OTHER=[[light, natural], [tasty, salty], [greasy, fried], [fresh, natural], [fresh]]}
  //
  ////        java 9
  //        Map<Dish.Type, Set<String>> dishNamesByType =
  //        menu.stream()
  //                        .collect(groupingBy(Dish::getType,
  //        flatMapping(dish -> dishTags.get( dish.getName() ).stream(),
  //        toSet())));
  //        System.out.println("populateDishTagsGroupByDishTypeUsingJava9 ==> " + dishNamesByType);
  //        // populateDishTagsGroupByDishTypeUsingJava9 ==> {FISH=[roasted, tasty, fresh,
  // delicious], MEAT=[salty, greasy, roasted, fried, crisp],
  //        //    OTHER=[salty, greasy, natural, light, tasty, fresh, fried]}
  //
  //    }

  private void totalCroupByDishType() {
    System.out.println(
        "totalCroupByDishType ==> "
            + menu.stream()
                .collect(
                    Collectors.groupingBy(
                        Dish::getType, Collectors.summingInt(Dish::getCalories))));
    //        totalCroupByDishType ==> {FISH=750, OTHER=1550, MEAT=1900}
  }

  private void groupByDishType() {
    Map<Dish.Type, List<Dish>> groupByDishType = menu.stream().collect(groupingBy(Dish::getType));
    System.out.println("groupByDishType =====> " + groupByDishType);
    //        groupByDishType =====> {OTHER=[french fries, rice, season fruit, pizza, Water],
    // MEAT=[pork, beef, chicken], FISH=[prawns, salmon]}
  }

  private void noOfDishesUsingMapAndReduce() {

    int noOfDishesUsingMapAndReduce =
        menu.stream().mapToInt(l -> 1).reduce(Integer::sum).getAsInt();
    System.out.println("noOfDishesUsingMapAndReduce ===> " + noOfDishesUsingMapAndReduce);
  }

  private void groupByCaloriesType() {

    Map<CaloriesType, List<Dish>> groupByCaloriesType =
        menu.stream().collect(groupingBy(getDishCaloriesType()));
    System.out.println("groupByCaloriesType ===> " + groupByCaloriesType);
  }

  private Function<Dish, CaloriesType> getDishCaloriesType() {
    return d -> {
      if (d.calories <= 400) return CaloriesType.LOW;
      else if (d.calories > 400 && d.calories <= 500) return CaloriesType.MEDIUM;
      else return CaloriesType.HIGH;
    };
  }

  private void averageOrSummarizingCalories() {
    System.out.println(
        "averageCalories ==> " + menu.stream().collect(Collectors.averagingInt(Dish::getCalories)));
    //        averageCalories ==> 420.0
    System.out.println(
        "summarizingCalories ==> "
            + menu.stream().collect(Collectors.summarizingInt(Dish::getCalories)));
    //        summarizingCalories ==> IntSummaryStatistics{count=10, sum=4200, min=0,
    // average=420.000000, max=800}
    System.out.println(
        "summarizingInt ==> " + menu.stream().collect(Collectors.summingInt(Dish::getCalories)));
    //        summarizingInt ==> 4200
    System.out.println(
        "summarizingInt ==> "
            + menu.stream().collect(Collectors.reducing(0, Dish::getCalories, (i, j) -> i + j)));
    //        summarizingInt ==> 4200
  }

  private void countingNumberOfDish() {
    System.out.println("count => " + menu.stream().count());
    // count => 10
    System.out.println("collect and counting => " + menu.stream().collect(Collectors.counting()));
    //        collect and counting => 10
    System.out.println(
        "group by Dish type and counting => "
            + menu.stream().collect(groupingBy(Dish::getType, Collectors.counting())));
    //        group by Dish type and counting => {FISH=2, OTHER=5, MEAT=3}
  }

  //    private void groupDishesWithDishTypeWithMoreThan500CaloriesUsingJava9() {
  //        // java 9
  //        Map<Dish.Type, List<Dish>> groupDishesWithDishTypeWithMoreThan500CaloriesUsingJava9 =
  //                menu.stream()
  //                        .collect(groupingBy(Dish::getType,
  //                                filtering(dish -> dish.getCalories() > 500, toList())));
  //        System.out.println("groupDishesWithDishTypeWithMoreThan500CaloriesUsingJava9 => " +
  // groupDishesWithDishTypeWithMoreThan500CaloriesUsingJava9 );
  ////        {OTHER=[french fries, pizza], MEAT=[pork, beef], FISH=[]}
  //
  //    }

  private void findDishWithHighestCaloriesPartitionByVegetarian() {
    //        Map implementation returned by
    //        partitioningBy is more compact and efficient as it only needs to contain two keys:
    //        true and false.
    Map<Boolean, Dish> mostCaloricPartitionedByVegetarian =
        menu.stream()
            .collect(
                partitioningBy(
                    Dish::isVegetarian,
                    collectingAndThen(maxBy(comparingInt(Dish::getCalories)), Optional::get)));
    System.out.println(
        "findDishWithHighestCaloriesPartitionByVegetarian ==> "
            + mostCaloricPartitionedByVegetarian);
    //        findDishWithHighestCaloriesPartitionByVegetarian ==> {false=pork, true=pizza}
  }

  private void findDishWithHighestCaloriesForEachDishType() {
    Map<Dish.Type, Optional<Dish>> mostCaloricByType =
        menu.stream()
            .collect(
                Collectors.groupingBy(
                    Dish::getType, Collectors.maxBy(comparingInt(Dish::getCalories))));
    System.out.println("mostCaloricByType ==> " + mostCaloricByType);
    Map<Dish.Type, Dish> highestCaloriesForEachDishType =
        menu.stream()
            .collect(
                Collectors.groupingBy(
                    Dish::getType,
                    Collectors.collectingAndThen(
                        Collectors.maxBy(comparingInt(Dish::getCalories)), Optional::get)));
    System.out.println("highestCaloriesForEachDishType ==> " + highestCaloriesForEachDishType);
    //        mostCaloricByType ==> {FISH=Optional[salmon], OTHER=Optional[pizza],
    // MEAT=Optional[pork]}
    //        highestCaloriesForEachDishType ==> {FISH=salmon, OTHER=pizza, MEAT=pork}

  }

  private void sortDishComparingUsingCaloriesAndThenComparingByName() {

    List<Dish> sortDishComparingUsingCaloriesAndThenComparingByName =
            menu.stream().sorted(Comparator.comparing(Dish::getCalories).thenComparing(Dish::getName)).collect(toList());
    System.out.println("sortDishComparingUsingCaloriesAndThenComparingByName ===> " + sortDishComparingUsingCaloriesAndThenComparingByName);
//    sortDishComparingUsingCaloriesAndThenComparingByName ===> [Water, season fruit, prawns, rice, chicken, salmon, french fries, pizza, beef, pork]

  }
  private void findDishWithHighestCalories() {

    Optional<Dish> highCalorieDish =
        menu.stream().collect(Collectors.maxBy(Comparator.comparing(Dish::getCalories)));
    System.out.println("highCalorieDish ===> " + highCalorieDish.get());
    //        highCalorieDish ===> pork

  Optional<Dish> highCalorieDishUsingReduce =
        menu.stream()
            .collect(
                Collectors.reducing((d1, d2) -> d1.getCalories() > d2.getCalories() ? d1 : d2));
    System.out.println("highCalorieDishUsingReduce ===> " + highCalorieDishUsingReduce.get());
    //        highCalorieDishUsingReduce ===> pork
  }

  private void groupByVegetarian() {
    Map<Boolean, List<Dish>> groupByVegetarian =
        menu.stream().collect(groupingBy(Dish::isVegetarian));
    System.out.println("groupByVegetarian ==> " + groupByVegetarian);
    //        groupByVegetarian ==> {false=[pork, beef, chicken, prawns, salmon], true=[french
    // fries, rice, season fruit, pizza, Water]}
    Map<Boolean, List<Dish>> partitionedMenuByVegetarian =
        menu.stream().collect(Collectors.partitioningBy(Dish::isVegetarian));
    System.out.println("partitionedMenuByVegetarian ==> " + partitionedMenuByVegetarian);
    //        partitionedMenuByVegetarian ==> {false=[pork, beef, chicken, prawns, salmon],
    // true=[french fries, rice, season fruit, pizza, Water]}

    Map<Boolean, List<Dish>> groupByVegetarianToUnmodifiableList =
        menu.stream().collect(groupingBy(Dish::isVegetarian, toUnmodifiableList()));
    System.out.println(
        "groupByVegetarianToUnmodifiableList ==> " + groupByVegetarianToUnmodifiableList);
    // groupByVegetarianToUnmodifiableList ==> {false=[pork, beef, chicken, prawns, salmon],
    // true=[french fries, rice, season fruit, pizza, Water]}
  }

  private void sumInDifferentWays() {
    System.out.println(
        "summarizingInt ==> "
            + menu.stream().collect(Collectors.summarizingInt(Dish::getCalories)));
    //        summarizingInt ==> IntSummaryStatistics{count=10, sum=4200, min=0, average=420.000000,
    // max=800
    System.out.println(
        "reducing Integer sum ==> "
            + menu.stream().collect(Collectors.reducing(0, Dish::getCalories, Integer::sum)));
    //        reducing Integer sum ==> 4200
    System.out.println(
        "reducing add ==> "
            + menu.stream().collect(Collectors.reducing(0, Dish::getCalories, (x, y) -> x + y)));
    //        reducing add ==> 4200
    System.out.println("mapToInt sum ==> " + menu.stream().mapToInt(Dish::getCalories).sum());
    //        mapToInt sum ==> 4200
    System.out.println(
        "map and reduce ==> " + menu.stream().map(Dish::getCalories).reduce(Integer::sum).get());
    //        map and reduce ==> 4200
  }

  private void findNumberOfDishesGroupByDishType() {
    System.out.println(
        "findNumberOfDishesGroupByDishType option 1 ==> "
            + menu.stream()
                .collect(
                    Collectors.groupingBy(
                        Dish::getType,
                        Collectors.collectingAndThen(Collectors.counting(), Function.identity()))));
    //        findNumberOfDishesGroupByDishType option 1 ==> {FISH=2, OTHER=5, MEAT=3}
    System.out.println(
        "findNumberOfDishesGroupByDishType option 2 ==> "
            + menu.stream().collect(Collectors.groupingBy(Dish::getType, Collectors.counting())));
    //        findNumberOfDishesGroupByDishType option 2 ==> {FISH=2, OTHER=5, MEAT=3}
  }

  private void groupIsVegetarianAndByDishType() {
    System.out.println(
        "groupIsVegetarianAndByDishType using group by==> "
            + menu.stream()
                .collect(
                    Collectors.groupingBy(
                        Dish::isVegetarian, Collectors.groupingBy(Dish::getType))));
    //        groupIsVegetarianAndByDishType using group by==> {false={FISH=[prawns, salmon],
    // MEAT=[pork, beef, chicken]}, true={OTHER=[french fries, rice, season fruit, pizza, Water]}}
    System.out.println(
        "Or groupIsVegetarianAndByDishType using group by==> "
            + menu.stream()
                .collect(
                    Collectors.groupingBy(
                        Dish::getType,
                        Collectors.collectingAndThen(groupingBy(Dish::isVegetarian), t -> t))));
    //        Or groupIsVegetarianAndByDishType using group by==> {FISH={false=[prawns, salmon]},
    // MEAT={false=[pork, beef, chicken]}, OTHER={true=[french fries, rice, season fruit, pizza,
    // Water]}}
    System.out.println(
        "groupIsVegetarianAndByDishType using partition ==> "
            + menu.stream()
                .collect(
                    Collectors.partitioningBy(
                        Dish::isVegetarian, Collectors.groupingBy(Dish::getType))));
    //        groupIsVegetarianAndByDishType using partition ==> {false={FISH=[prawns, salmon],
    // MEAT=[pork, beef, chicken]}, true={OTHER=[french fries, rice, season fruit, pizza, Water]}}

  }

  private void groupByDishTypeAndIsVegetarian() {

    System.out.println(
        "groupByDishTypeAndIsVegetarian ==> "
            + menu.stream()
                .collect(
                    groupingBy(
                        Dish::getType,
                        Collectors.collectingAndThen(groupingBy(Dish::isVegetarian), t -> t))));
    //        groupByDishTypeAndIsVegetarian ==> {FISH={false=[prawns, salmon]}, MEAT={false=[pork,
    // beef, chicken]}, OTHER={true=[french fries, rice, season fruit, pizza, Water]}}
    System.out.println(
        "OR groupByDishTypeAndIsVegetarian ==> "
            + menu.stream().collect(groupingBy(Dish::getType, groupingBy(Dish::isVegetarian))));
    //        OR groupByDishTypeAndIsVegetarian ==> {FISH={false=[prawns, salmon]},
    // MEAT={false=[pork, beef, chicken]}, OTHER={true=[french fries, rice, season fruit, pizza,
    // Water]}}

  }

  private void calorieLevelByType() {

    System.out.println(
        "calorieLevelByType option 1 ===> "
            + menu.stream()
                .collect(
                    Collectors.groupingBy(
                        Dish::getType,
                        Collectors.mapping(getDishCaloriesType(), Collectors.toSet()))));
    System.out.println(
        "calorieLevelByType option 2 ===> "
            + menu.stream()
                .collect(
                    Collectors.groupingBy(
                        Dish::getType,
                        Collectors.mapping(
                            getDishCaloriesType(), Collectors.toCollection(HashSet::new)))));
    //        calorieLevelByType option 1 ===> {MEAT=[HIGH, LOW], OTHER=[HIGH, LOW], FISH=[MEDIUM,
    // LOW]}
    //        calorieLevelByType option 2 ===> {MEAT=[HIGH, LOW], OTHER=[HIGH, LOW], FISH=[MEDIUM,
    // LOW]}

  }

  private void fibonacciSequence() {

    // (0,1), (1,2), (2,3),(3,5), (5,8), (8,13),
    // 1, 1, 2, 3, 5, 8, 13
    Stream.iterate(new int[] {0, 1}, t -> new int[] {t[1], t[0] + t[1]})
        .limit(10)
        .forEach(x -> System.out.print(x[1] + ","));
  }

  private void primeNumbers() {
    // print first 10 prime numbers
    int start = 0;
    int end = 20;

    // Traditional method
    for (int i = start; i < end; i++) {
      if (isPrimeNumber(i)) {
        System.out.println(i + " is a prime number (Traditional method)");
      }
    }

    // Using Lamda
    for (int i = start; i < end; i++) {
      if (isPrimeUsingLamdas(i)) {
        System.out.println(i + " is a prime number (Using Lamda)");
      }
    }

    List<Integer> listOfPrimeNumbers = new ArrayList<>();
    // Using Lamda with optimzation
    for (int i = start; i < end; i++) {
      if (isPrimeUsingLamdaWithOptimization(listOfPrimeNumbers, i)) {
        System.out.println(i + " is a prime number (Using Lamda with optimzation)");
        listOfPrimeNumbers.add(i);
      }
    }
  }

  private boolean isPrimeNumber(int input) {
    boolean isPrime = true;
    if (input <= 1) isPrime = false;
    // Traditional for loop
    for (int i = 2; i <= Math.sqrt(input); i++) {
      if (input % i == 0) {
        // divisible. Not a prime.
        isPrime = false;
        break;
      }
    }

    return isPrime;
  }

  public boolean isPrimeUsingLamdas(int candidate) {

    if (candidate <= 1) return false;
    int candidateRoot = (int) Math.sqrt((double) candidate);
    // Using Lamda
    return IntStream.rangeClosed(2, candidateRoot).noneMatch(i -> candidate % i == 0);
  }

  public boolean isPrimeUsingLamdaWithOptimization(
      List<Integer> listOfPrimeNumbers, int candidate) {

    if (candidate <= 1) return false;
    int candidateRoot = (int) Math.sqrt((double) candidate);

    return listOfPrimeNumbers.stream()
        .filter(i -> i <= candidateRoot)
        .noneMatch(i -> candidate % i == 0);
    // java 9
    //        return listOfPrimeNumbers.stream().takeWhile(i -> i<=candidateRoot)
    //                .noneMatch(i -> candidate % i == 0);

  }

  private void countByVegetarian() {

    System.out.println(
        "countByVegetarian using group by==> "
            + menu.stream()
                .collect(Collectors.groupingBy(Dish::isVegetarian, Collectors.counting())));
    //        countByVegetarian using group by==> {false=5, true=5}
    System.out.println(
        "countByVegetarian using partition ==> "
            + menu.stream()
                .collect(Collectors.partitioningBy(Dish::isVegetarian, Collectors.counting())));
    //        countByVegetarian using partition ==> {false=5, true=5}
  }

  private void groupByDishTypeAndVegetarianAndCalorieType() {

    System.out.println(
        "groupByDishTypeAndVegetarianAndCalorieType ===> "
            + menu.stream()
                .collect(
                    groupingBy(
                        Dish::getType,
                        groupingBy(Dish::isVegetarian, groupingBy(getDishCaloriesType())))));
    //        groupByDishTypeAndVegetarianAndCalorieType ===> {FISH={false={MEDIUM=[salmon],
    // LOW=[prawns]}}, OTHER={true={LOW=[rice, season fruit, Water], HIGH=[french fries, pizza]}},
    // MEAT={false={LOW=[chicken], HIGH=[pork, beef]}}}

  }

  private void collectionFactoryExamples() {

    try {
      List<String> friends = Arrays.asList("Raphael", "Olivia"); // Cannot add, However can modify
      friends.set(0, "Richard");
      friends.add("Thibaut"); // Throws UnsupportedOperationException
      System.out.println("friends using Arrays.asList ==> " + friends);
    } catch (RuntimeException use) {
      use.printStackTrace();
    }

    try {
      List<String> friendsUsingListOf =
          List.of("Raphael", "Olivia", "Thibaut"); // produces Immutable list
      System.out.println("java9 friendsUsingListOf ==> " + friendsUsingListOf);
      friendsUsingListOf.add("exception");
      friendsUsingListOf.set(0, "exception"); // This will throw UnsupportedOperationException
    } catch (UnsupportedOperationException use) {
      System.out.println(use.getMessage());
    }
    try {
      Set<String> friends = Set.of("Raphael", "Olivia", "Thibaut");
      System.out.println(friends);
    } catch (UnsupportedOperationException use) {
      System.out.println(use.getMessage());
    }

    Map<String, Integer> ageOfFriends = Map.of("Raphael", 30, "Olivia", 25, "Thibaut", 26);
    System.out.println(ageOfFriends);

    Map<String, Integer> ageOfFriendsUsingMapEntry =
        Map.ofEntries(entry("Raphael", 30), entry("Olivia", 25), entry("Thibaut", 26));
    System.out.println(ageOfFriendsUsingMapEntry);
  }

  private void usingMapGetDefaultMethod() {

    Map<String, String> favouriteMovies =
        Map.ofEntries(entry("Raphael", "Star Wars"), entry("Olivia", "James Bond"));
    System.out.println(favouriteMovies.getOrDefault("Olivia", "Matrix"));
    System.out.println(favouriteMovies.getOrDefault("Thibaut", "Matrix"));
  }

  enum CaloriesType {
    LOW,
    MEDIUM,
    HIGH
  }

  List<Dish> menu = null;
  Map<String, List<String>> dishTags = null;

  public LamdasExamples() {
    menu =
        Arrays.asList(
            new Dish("pork", false, 800, Dish.Type.MEAT),
            new Dish("beef", false, 700, Dish.Type.MEAT),
            new Dish("chicken", false, 400, Dish.Type.MEAT),
            new Dish("french fries", true, 550, Dish.Type.OTHER),
            new Dish("rice", true, 350, Dish.Type.OTHER),
            new Dish("season fruit", true, 120, Dish.Type.OTHER),
            new Dish("A1 pizza", true, 550, Dish.Type.OTHER),
            new Dish("prawns", false, 300, Dish.Type.FISH),
            new Dish("Water", true, 0, Dish.Type.OTHER),
            new Dish("salmon", false, 450, Dish.Type.FISH));

    System.out.println("menu ===> " + menu);
    dishTags = new HashMap<>();
    dishTags.put("pork", asList("greasy", "salty"));
    dishTags.put("beef", asList("salty", "roasted"));
    dishTags.put("chicken", asList("fried", "crisp"));
    dishTags.put("french fries", asList("greasy", "fried"));
    dishTags.put("rice", asList("light", "natural"));
    dishTags.put("season fruit", asList("fresh", "natural"));
    dishTags.put("pizza", asList("tasty", "salty"));
    dishTags.put("prawns", asList("tasty", "roasted"));
    dishTags.put("salmon", asList("delicious", "fresh"));
    dishTags.put("Water", asList("fresh"));
  }

  static class Dish {
    private final String name;
    private final boolean vegetarian;
    private final int calories;
    private final Type type;

    public Dish(String name, boolean vegetarian, int calories, Type type) {
      this.name = name;
      this.vegetarian = vegetarian;
      this.calories = calories;
      this.type = type;
    }

    public String getName() {
      return name;
    }

    public boolean isVegetarian() {
      return vegetarian;
    }

    public int getCalories() {
      return calories;
    }

    public Type getType() {
      return type;
    }

    @Override
    public String toString() {
      return name;
    }

    enum Type {
      MEAT,
      FISH,
      OTHER,
      EGG
    }
  }
}
