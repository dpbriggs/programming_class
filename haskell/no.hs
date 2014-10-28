fact 0 = 1
fact 1 = 1
fact n = n * fact(n - 1)

fizzbuzz n
  | n `mod` 15 == 0 = "fizzbuzz"
  | n `mod` 5 == 0 = "buzz"
  | n `mod` 3 == 0 = "fizz"
