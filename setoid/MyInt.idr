module MyInt

import Setoid

%default total
%access public export

data MyInt = Sub Nat Nat

plus : MyInt -> MyInt -> MyInt
plus (Sub a b) (Sub c d) = Sub (a + c) (b + d)

mult : MyInt -> MyInt -> MyInt
mult (Sub a b) (Sub c d) = Sub (a * c + b * d) (a * d + b * c)

neg : MyInt -> MyInt
neg (Sub a b) = Sub b a

minus : MyInt -> MyInt -> MyInt
minus (Sub a b) (Sub c d) = Sub (a + d) (b + c)

data Sign: MyInt -> Type where
  LessZero    : {a: Nat} -> {b: Nat} -> {x:Nat} -> (pr: a + S x = b) -> Sign $ Sub a b
  GreaterZero : {a: Nat} -> {b: Nat} -> {x:Nat} -> (pr: a = b + S x) -> Sign $ Sub a b
  IsZero      : {a: Nat} -> {b: Nat}            -> (pr: a = b)       -> Sign $ Sub a b

sign: (x: MyInt) -> Sign x
sign (Sub Z Z)              = IsZero Refl
sign (Sub Z (S t))          = LessZero $ Refl
sign (Sub (S t) Z)          = GreaterZero $ Refl
sign (Sub (S x) (S y)) with (sign (assert_smaller (Sub (S x) (S y)) (Sub x y)))
         | (LessZero pr)    = LessZero $ cong pr
         | (GreaterZero pr) = GreaterZero $ cong pr
         | (IsZero pr)      = IsZero $ cong pr


implementation Num MyInt where
    (+) = plus
    (*) = mult
    fromInteger n = if n > 0 then Sub (fromInteger n) Z else Sub Z (fromInteger $ abs n)

implementation Neg MyInt where
    negate = neg
    (-)    = minus

multN : MyInt -> Nat -> MyInt
multN (Sub a b) k = Sub (a * k) (b * k)

data EqInt : Relation MyInt where
    ReflInt : (eq : a + d = c + b) -> EqInt (Sub a b) (Sub c d)

intRefl : Rfl EqInt
intRefl (Sub a b) = ReflInt Refl

intSymm : Symm EqInt
intSymm (Sub a b) (Sub c d) (ReflInt eq) = ReflInt $ sym eq

intTrans : Trans EqInt
intTrans x@(Sub a b) y@(Sub c d) z@(Sub e f) (ReflInt xyEq) (ReflInt yzEq) = ReflInt $ expr0
    where
        expr9 : (a + d) + (c + f) = (b + c) + (d + e)
        expr8 : (d + a) + (c + f) = (b + c) + (d + e)
        expr7 : d + (a + (c + f)) = (b + c) + (d + e)
        expr6 : (a + (c + f)) + d = (b + c) + (d + e)
        expr5 : (a + (c + f)) + d = (b + c) + (e + d)
        expr4 : (a + (c + f)) + d = ((b + c) + e) + d
        expr3 : a + (c + f)       = (b + c) + e
        expr2 : (a + f) + c       = (b + e) + c
        expr1 : (a + f)           = (b + e)
        expr0 : (a + f)           = (e + b)

        expr9 =
            rewrite xyEq in
            rewrite yzEq in
            rewrite plusCommutative b c in
            rewrite plusCommutative d e in Refl
        expr8 =
            rewrite plusCommutative d a in expr9
        expr7 =
            rewrite plusAssociative d a (c + f) in expr8
        expr6 =
            rewrite plusCommutative (a + (c + f)) d in expr7
        expr5 =
            rewrite plusCommutative e d in expr6
        expr4 =
            rewrite sym $ plusAssociative (b + c) e d in expr5
        expr3 =
            plusRightCancel (a + (c + f)) ((b + c) + e) d expr4
        expr2 =
            rewrite sym $ plusAssociative a f c in
            rewrite plusCommutative f c in
            rewrite sym $ plusAssociative b e c in
            rewrite plusCommutative e c in
            rewrite plusAssociative b c e in expr3
        expr1 =
            plusRightCancel (a + f) (b + e) c expr2
        expr0 =
            rewrite plusCommutative e b in expr1

IntSetoid : Setoid
IntSetoid = MkSetoid MyInt EqInt $ MkEquality intRefl intSymm intTrans
