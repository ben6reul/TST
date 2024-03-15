import scala.util.Try

/*

Background: Cruise bookings can have one or more Promotions applied to them. But sometimes a Promotion cannot be combined with another Promotion.
 Our application has to find out all possible Promotion Combinations that can be applied together.
1. Implement a function to find all PromotionCombos with maximum number of combinable promotions in each. The function and case class definitions are supplied below to get you started.

2. Implement a function to find all PromotionCombos for a given Promotion from given list of Promotions. The function definition is provided

 */

case class Promotion(code: String, notCombinableWith: Seq[String])
case class PromotionCombo(promotionCodes: Seq[String])

def allCombinablePromotions(
    allPromotions: Seq[Promotion]
): Seq[PromotionCombo] = {
  val filtered: Set[Set[String]] = allPromotions
    .map(_.code)
    .toSet
    .subsets()
    .filterNot(subset =>
      allPromotions.exists(ele =>
        subset.contains(ele.code) && subset
          .intersect(ele.notCombinableWith.toSet)
          .nonEmpty
      ) || subset.isEmpty
    )
    .toSet

  filtered
    .filterNot(combo =>
      filtered.exists(originalCombo =>
        originalCombo != combo && combo.subsetOf(originalCombo)
      )
    )
    .map(combo => PromotionCombo(combo.toSeq))
    .toSeq
}
def combinablePromotions(
    promotionCode: String,
    allPromotions: Seq[Promotion]
): Seq[PromotionCombo] = {
  allCombinablePromotions(allPromotions).filter(promo =>
    promo.promotionCodes.contains(promotionCode)
  )
}

val promoList = Seq(
  Promotion("P1", Seq("P3")), // P1 is not combinable with P3
  Promotion("P2", Seq("P4", "P5")), // P2 is not combinable with P4 and P5
  Promotion("P3", Seq("P1")), // P3 is not combinable with P1
  Promotion("P4", Seq("P2")), // P4 is not combinable with P2
  Promotion("P5", Seq("P2")) // P5 is not combinable with P2
)

val combinablePromotionsTests
    : Seq[(String, Seq[Promotion], String, Seq[PromotionCombo])] = Seq(
  (
    "Happy Path P1",
    promoList,
    "P1",
    Seq(
      PromotionCombo(Seq("P1", "P2")),
      PromotionCombo(Seq("P1", "P4", "P5"))
    )
  ),
  (
    "Empty Promo List",
    Seq.empty,
    "P1",
    Seq.empty
  ),
  (
    "Promo Not Found",
    promoList,
    "P99",
    Seq.empty
  ),
  (
    "Happy Path P3",
    promoList,
    "P3",
    Seq(
      PromotionCombo(Seq("P3", "P2")),
      PromotionCombo(Seq("P3", "P4", "P5"))
    )
  )
)

// Iterate over test cases and run them
println("Combinable Promotions Tests")
combinablePromotionsTests.foreach {
  case (testDescription, testPromoList, inputPromo, expectedOutput) =>
    println(testDescription)
    val output = Try(combinablePromotions(inputPromo, testPromoList))
    if (
      output.isSuccess && output.get
        .map(_.promotionCodes.toSet)
        .toSet == expectedOutput.map(_.promotionCodes.toSet).toSet
    ) {
      println("Test passed!")
    } else {
      println("Test failed!")
    }
}

val AllCombinablePromotionsTests
    : Seq[(String, Seq[Promotion], Seq[PromotionCombo])] = Seq(
  (
    "Happy Path",
    promoList,
    Seq(
      PromotionCombo(Seq("P1", "P2")),
      PromotionCombo(Seq("P1", "P4", "P5")),
      PromotionCombo(Seq("P2", "P3")),
      PromotionCombo(Seq("P3", "P4", "P5"))
    )
  ),
  (
    "No Promo",
    Seq.empty,
    Seq.empty
  )
)

println("All Combinable Promotions Tests")
AllCombinablePromotionsTests.foreach {
  case (testDescription, testPromoList, expectedOutput) =>
    println(testDescription)
    val output = Try(allCombinablePromotions(testPromoList))
    if (
      output.isSuccess && output.get
        .map(_.promotionCodes.toSet)
        .toSet == expectedOutput.map(_.promotionCodes.toSet).toSet
    ) {
      println("Test passed!")
    } else {
      println("Test failed!")
    }
}
