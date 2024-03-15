import scala.util.Try

case class Rate(rateCode: String, rateGroup: String)

case class CabinPrice(cabinCode: String, rateCode: String, price: BigDecimal)

case class BestGroupPrice(
    cabinCode: String,
    rateCode: String,
    price: BigDecimal,
    rateGroup: String
)

def getBestGroupPrices(
    rates: Seq[Rate],
    prices: Seq[CabinPrice]
): Seq[BestGroupPrice] = {

  //if a rating maps to multiple groups, assume first group found is correct
  val rateMap: Map[String, String] =
    rates.groupMapReduce(rate => rate.rateCode)(rate => rate.rateGroup)(
      (r, _) => r
    )

  prices
    .flatMap(prices =>
      rateMap
        // attempt to get mapping, if it doesn't exist we will flat map the option out
        .get(prices.rateCode)
        .map(rateGroup =>
          BestGroupPrice(
            prices.cabinCode,
            prices.rateCode,
            prices.price,
            rateGroup
          )
        )
    )
    .groupMapReduce(bestPrice => (bestPrice.cabinCode, bestPrice.rateGroup))(
      v => v
    )((v1, v2) => if (v1.price <= v2.price) v1 else v2)
    .values
    .toSeq
}

//Tests
val tests: Seq[(String, Seq[Rate], Seq[CabinPrice], Seq[BestGroupPrice])] = Seq(
  (
    "Test: Happy path to match expected output ",
    Seq(
      Rate("M1", "Military"),
      Rate("M2", "Military"),
      Rate("S1", "Senior"),
      Rate("S2", "Senior")
    ),
    Seq(
      CabinPrice("CA", "M1", 200.00),
      CabinPrice("CA", "M2", 250.00),
      CabinPrice("CA", "S1", 225.00),
      CabinPrice("CA", "S2", 260.00),
      CabinPrice("CB", "M1", 230.00),
      CabinPrice("CB", "M2", 260.00),
      CabinPrice("CB", "S1", 245.00),
      CabinPrice("CB", "S2", 270.00)
    ),
    Seq(
      BestGroupPrice("CA", "M1", 200.00, "Military"),
      BestGroupPrice("CA", "S1", 225.00, "Senior"),
      BestGroupPrice("CB", "M1", 230.00, "Military"),
      BestGroupPrice("CB", "S1", 245.00, "Senior")
    )
  ),
  (
    "Test: Expect multiple entries for same mappings from rate code to rate group to return best prices",
    Seq(
      Rate("M1", "Military"),
      Rate("M1", "Military"),
      Rate("S1", "Senior"),
      Rate("S2", "Senior")
    ),
    Seq(
      CabinPrice("CA", "M1", 200.00),
      CabinPrice("CA", "S1", 225.00),
      CabinPrice("CA", "S2", 260.00),
      CabinPrice("CB", "M1", 230.00),
      CabinPrice("CB", "S1", 245.00),
      CabinPrice("CB", "S2", 270.00)
    ),
    Seq(
      BestGroupPrice("CA", "M1", 200.00, "Military"),
      BestGroupPrice("CA", "S1", 225.00, "Senior"),
      BestGroupPrice("CB", "M1", 230.00, "Military"),
      BestGroupPrice("CB", "S1", 245.00, "Senior")
    )
  ),
  (
    "Test: Expect multiple mappings from rate code to rate group to assume first instance correct and return best prices",
    Seq(
      Rate("M1", "Military"),
      Rate("M2", "Military"),
      Rate("M1", "Senior"),
      Rate("M2", "Senior")
    ),
    Seq(
      CabinPrice("CA", "M1", 200.00),
      CabinPrice("CB", "M1", 230.00)
    ),
    Seq(
      BestGroupPrice("CA", "M1", 200.00, "Military"),
      BestGroupPrice("CB", "M1", 230.00, "Military")
    )
  ),
  (
    "Test: Expect Empty prices + rate mapping to give no best prices",
    Seq.empty,
    Seq.empty,
    Seq.empty
  ),
  (
    "Test: Expect Empty prices to give no best prices",
    Seq(
      Rate("M1", "Military"),
      Rate("M2", "Military"),
      Rate("M1", "Senior"),
      Rate("M2", "Senior")
    ),
    Seq.empty,
    Seq.empty
  ),
  (
    "Test: Expect Empty rate mappings to give no best prices",
    Seq.empty,
    Seq(
      CabinPrice("CA", "M1", 200.00),
      CabinPrice("CB", "M1", 230.00)
    ),
    Seq.empty
  ),
  (
    "Test: Expect multiple entries for same room prices to return best prices",
    Seq(
      Rate("M1", "Military"),
      Rate("M1", "Military"),
      Rate("S1", "Senior"),
      Rate("S2", "Senior")
    ),
    Seq(
      CabinPrice("CA", "M1", 200.00),
      CabinPrice("CA", "M1", 200.00),
      CabinPrice("CA", "S1", 225.00),
      CabinPrice("CA", "S1", 225.00)
    ),
    Seq(
      BestGroupPrice("CA", "M1", 200.00, "Military"),
      BestGroupPrice("CA", "S1", 225.00, "Senior")
    )
  ),
  (
    "Test: Expect multiple mappings from rate code to rate group to assume first instance correct and return best prices",
    Seq(
      Rate("M1", "Military"),
      Rate("M2", "Military"),
      Rate("M1", "Senior"),
      Rate("M2", "Senior")
    ),
    Seq(
      CabinPrice("CA", "M1", 200.00),
      CabinPrice("CB", "M1", 230.00)
    ),
    Seq(
      BestGroupPrice("CA", "M1", 200.00, "Military"),
      BestGroupPrice("CB", "M1", 230.00, "Military")
    )
  )
)
// Iterate over test cases and run them
tests.foreach { case (testDescription, rates, prices, expectedOutput) =>
  println(testDescription)
  val output = Try(getBestGroupPrices(rates, prices))
  if (output.isSuccess && output.get.toSet == expectedOutput.toSet) {
    println("Test passed!")
  } else {
    println("Test failed!")
  }
}
