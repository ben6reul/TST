
## Running: 
I have written these in scala worksheets. They can be run according to the docs: https://docs.scala-lang.org/scala3/book/tools-worksheets.html

## Problem 1:

### Assumptions Made in the Code:
Given the problem statement I did not assume an intended output ordering. In my tests I sort the sequences to compare them.
Another option would be to convert the sequences to a sets but that would lose the potential test case of the function outputting 
multiple instances of the same mappings.


I also assumed that if the input rate code to rate group mapping was malformed and had multiple code -> group mappings
that the first instance found in the sequence was the correct one. Another option would be to throw an exception.

### Test Cases Covered:

1. Happy path to match expected output
2. Expect multiple entries for the same mappings from rate code to rate group to return best prices
3. Expect multiple mappings from rate code to rate group to assume the first instance correct and return best prices
4. Expect empty prices + rate mapping to give no best prices
5. Expect empty prices to give no best prices
6. Expect empty rate mappings to give no best prices
7. Expect multiple entries for the same room prices to still return best prices

### Comments
Some of the given models contain sequences when they should really be sets as order doesn't matter.

These string values should probably be enums.

## Problem 2:

### Assumptions Made in the Code:

Given the problem statement I did not assume an intended output ordering. In my tests I convert promotions to sets to compare them.

The promo list is well-formed, no double entries, or incorrect/missing entries

### Test Cases Covered:

#### combinablePromotionsTests:

1. Happy Path P1: Tests the scenario where combinable promotions for promotion code "P1" are correctly retrieved.
2. Empty Promo List: Tests the scenario where the input promotion list is empty, expecting an empty output.
3. Promo Not Found: Tests the scenario where the given promotion code does not exist in the promotion list, expecting an empty output.
4. Happy Path P3: Tests the scenario where combinable promotions for promotion code "P3" are correctly retrieved.

#### AllCombinablePromotionsTests:

1. Happy Path: Tests the scenario where all possible combinable promotions are correctly generated from the provided promotion list.
2. No Promo: Tests the scenario where the input promotion list is empty, expecting an empty output.

### Comments

This could almost certainly be done more efficiently by caching some of the lookups for subset, potentially even further by not generating all subsets and filtering but by building up the list instead.

Some of the given models contain sequences when they should really be sets as order doesn't matter.

These string values should probably be enums.
