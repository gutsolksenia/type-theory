module MyRat

import Setoid
import MyInt

%default total
%access public export

data MyRat: Type where
    Div : MyInt -> (n:Nat) -> Not (n = Z) -> MyRat

multZeroLeftNZRightZero : {a,b:Nat} -> (a * b = Z) -> Not (a = Z) -> (b = Z)
multZeroLeftNZRightZero {a=Z}             prf1 prf2  = void $ prf2 prf1
multZeroLeftNZRightZero {a=(S a)} {b=Z} prf1 prf2    = Refl
multZeroLeftNZRightZero {a=S a}{b=S b}   prf1 prf2   = void $ uninhabited prf1

multNZLeftCancel : {d, a, b: Nat} -> Not (d = 0) -> d * a = d * b -> a = b
multNZLeftCancel {a = Z} {b = Z} prfNZ prfEq            = Refl
multNZLeftCancel {d}{a = Z} {b = (S k)} prfNZ prfEq     = sym $ multZeroLeftNZRightZero y prfNZ
    where
        y : d * (S k) = 0
        y = sym $ rewrite sym $ multZeroRightZero d in prfEq
multNZLeftCancel {d} {a = (S k)} {b = Z} prfNZ prfEq    = multZeroLeftNZRightZero y prfNZ
    where
        y : d * (S k) = 0
        y = rewrite sym $ multZeroRightZero d in prfEq
multNZLeftCancel {d}{a = (S k)} {b = (S j)} prfNZ prfEq = cong $ multNZLeftCancel {d=d}{a=k} {b=j} prfNZ y2
    where
        y1 : d + d * k = d + d * j
        y1 =
            rewrite sym $ multRightSuccPlus d k in
            rewrite sym $ multRightSuccPlus d j in prfEq
        y2 : d * k = d * j
        y2 = plusLeftCancel d (d * k) (d * j) y1

multNZIsNZ : Not (a = Z) -> Not (b = Z) -> Not (a * b = Z)
multNZIsNZ prfLeft prfRight prfBoth = prfRight $ multZeroLeftNZRightZero prfBoth prfLeft

multSwapingRight : (x : Nat) -> (y : Nat) -> (z : Nat) -> (x * y) * z = (x * z) * y
multSwapingRight x y z =
    rewrite sym $ multAssociative x y z in
    rewrite multCommutative y z in
    rewrite multAssociative x z y in Refl

-- a / b + c / d = (a * d + c * b) / (b * d)
plus : MyRat -> MyRat -> MyRat
plus (Div a b prf1) (Div c d prf2) = Div (multN a d + multN c b) (b * d) (multNZIsNZ prf1 prf2)

mult : MyRat -> MyRat -> MyRat
mult (Div a b prf1) (Div c d prf2) = Div (a * c) (b * d) (multNZIsNZ prf1 prf2)

data MyRatIsNZ: MyRat -> Type where
    NZ : Not (x = y) -> MyRatIsNZ $ Div (Sub x y) q prf

toNatAbs : (a:Nat) -> (b:Nat) -> Not (a = b) -> (c:Nat ** Not (c = 0))
toNatAbs Z Z notEq = (Z ** notEq)
toNatAbs Z (S k) notEq = (S k ** uninhabited)
toNatAbs (S k) Z notEq = (S k ** uninhabited)
toNatAbs (S k) (S j) notEq = toNatAbs k j (notEq . cong)

div: MyRat -> (q:MyRat) -> MyRatIsNZ q -> MyRat
div (Div (Sub a b) x prfX) (Div (Sub c d) y prfY) (NZ nzPr) with (sign (Sub c d))
    | IsZero pr      = void (nzPr pr)
    | LessZero pr    = let (t ** prfT) = toNatAbs c d nzPr in Div (Sub (b*y) (a*y)) (x * t) (multNZIsNZ prfX prfT)
    | GreaterZero pr = let (t ** prfT) = toNatAbs c d nzPr in Div (Sub (a*y) (b*y)) (x * t) (multNZIsNZ prfX prfT)

neg : MyRat -> MyRat
neg (Div a b prf) = Div (negate a) b prf

-- a / b - c / d = (a * d - c * b) / (d * b)
minus : MyRat -> MyRat -> MyRat
minus (Div a b prf1) (Div c d prf2) = Div (multN a d - multN c b) (b * d) (multNZIsNZ prf1 prf2)

implementation Num MyRat where
    (+) = plus
    (*) = mult
    fromInteger n = Div (fromInteger n) 1 uninhabited

implementation Neg MyRat where
    negate = neg
    (-)    = minus

data EqRat : Relation MyRat where
    ReflRat : {a, c: MyInt} -> {d, b: Nat}
               -> (prfB: Not(b = Z))
               -> (prfD: Not(d = Z))
               -> (eq: multN a d `EqInt` multN c  b)
               -> EqRat (Div a b prfB) (Div c d prfD)

ratRefl : Rfl EqRat
ratRefl (Div a b prf) = ReflRat prf prf (intRefl (multN a b))

ratSymm : Symm EqRat
ratSymm (Div a b prfB) (Div c d prfD) (ReflRat _ _ eq) = ReflRat prfD prfB (intSymm (multN a d) (multN c b) eq)

ratTrans : Trans EqRat
ratTrans (Div (Sub a1 a2) b prfB) (Div (Sub c1 c2) d prfD) (Div (Sub e1 e2) f prfF)
         (ReflRat prfB prfD (ReflInt eq1)) (ReflRat prfD prfF (ReflInt eq2)) = ReflRat prfB prfF (ReflInt res)
    where
        eqSumDistributedMult : {u1, u2, v1, v2, x, y, z : Nat} -> (u1 * y) + (v2 * x) = (v1 * x) + (u2 * y) ->
                               (u1 * y) * z + (v2 * x) * z = (v1 * x) * z + (u2 * y) * z
        eqSumDistributedMult eq {u1 = u1} {u2 = u2} {v1 = v1} {v2 = v2} {x = x} {y = y} {z = z} =
            rewrite sym $ multDistributesOverPlusLeft (u1 * y) (v2 * x) (z) in
            rewrite sym $ multDistributesOverPlusLeft (v1 * x) (u2 * y) (z) in
            cong {f = (* z)} eq

        eqSum : {u : Nat} -> {v : Nat} -> {x : Nat} -> {y : Nat} ->
                (u = v) -> (x = y) -> (u + x = v + y)
        eqSum eq1 eq2 = rewrite eq1 in rewrite eq2 in Refl

        sum : ((a1 * d) * f + (c2 * b) * f) + ((c1 * f) * b + (e2 * d) * b) =
              ((c1 * b) * f + (a2 * d) * f) + ((e1 * d) * b + (c2 * f) * b)
        sum = eqSum (eqSumDistributedMult eq1) (eqSumDistributedMult eq2)

        exprC12ToLeft : ((c1 * f) * b  + (c2 * b) * f) + ((a1 * d) * f + (e2 * d) * b) =
                        ((c1 * b) * f + (a2 * d) * f) + ((e1 * d) * b + (c2 * f) * b)
        exprC12ToLeft = rewrite plusAssociative ((c1 * f) * b  + (c2 * b) * f) ((a1 * d) * f) ((e2 * d) * b) in
                 rewrite sym $ plusAssociative ((c1 * f) * b) ((c2 * b) * f) ((a1 * d) * f) in
                 rewrite plusCommutative ((c2 * b) * f) ((a1 * d) * f) in
                 rewrite plusCommutative ((c1 * f) * b) (((a1 * d) * f) + ((c2 * b) * f)) in
                 rewrite sym $ plusAssociative (((a1 * d) * f) + ((c2 * b) * f)) ((c1 * f) * b) ((e2 * d) * b) in sum

        p1 : Nat
        p1 = c1 * b * f
        p2 : Nat
        p2 = a2 * d * f
        p3 : Nat
        p3 = e1 * d * b
        p4 : Nat
        p4 = c2 * f * b

        exprC12ToLeftSecond : ((c1 * f) * b  + (c2 * b) * f) + ((a1 * d) * f + (e2 * d) * b) =
                              ((c1 * b) * f + (c2 * f) * b) + ((e1 * d) * b + (a2 * d) * f)
        exprC12ToLeftSecond = rewrite plusCommutative p3 p2 in
                  rewrite plusAssociative (p1 + p4) p2 p3 in
                  rewrite plusCommutative (p1 + p4) p2 in
                  rewrite plusAssociative p2 p1 p4 in
                  rewrite plusCommutative p2 p1 in
                  rewrite sym $ plusAssociative (p1 + p2) p4 p3 in
                  rewrite plusCommutative p4 p3 in exprC12ToLeft

        changeC1BFOrder : ((c1 * b) * f  + (c2 * f) * b) =
                          ((c1 * f) * b + (c2 * f) * b)
        changeC1BFOrder = rewrite sym $ multAssociative c1 b f in
                rewrite multCommutative b f in
                rewrite multAssociative c1 f b in
                Refl

        exprFirstAdjusted : ((c1 * f) * b  + (c2 * b) * f) + ((a1 * d) * f + (e2 * d) * b) =
                            ((c1 * f) * b + (c2 * f) * b) + ((e1 * d) * b + (a2 * d) * f)
        exprFirstAdjusted = rewrite sym $ changeC1BFOrder in exprC12ToLeftSecond

        exprC1FBDeleted : (c2 * b) * f + ((a1 * d) * f + (e2 * d) * b) =
                          (c2 * f) * b + ((e1 * d) * b + (a2 * d) * f)
        exprC1FBDeleted = plusLeftCancel ((c1 * f) * b) ((c2 * b) * f + ((a1 * d) * f + (e2 * d) * b)) ((c2 * f) * b + ((e1 * d) * b + (a2 * d) * f)) $
                        rewrite plusAssociative ((c1 * f) * b)  ((c2 * b) * f) ((a1 * d) * f + (e2 * d) * b) in
                        rewrite plusAssociative ((c1 * f) * b) ((c2 * f) * b) (((e1 * d) * b + (a2 * d) * f)) in exprFirstAdjusted

        changeOrderFirst : (c2 * b) * f + (v1 + v2) =
                           (c3 * f) * b + (v3 + v4) -> (c2 * f) * b + (v1 + v2) = (c3 * f) * b + (v3 + v4)
        changeOrderFirst eq = rewrite sym $ multAssociative c2 f b in
                     rewrite multCommutative f b in
                     rewrite multAssociative c2 b f in eq

        exprC2FBBothLeft : (c2 * f) * b + ((a1 * d) * f + (e2 * d) * b) =
                           (c2 * f) * b + ((e1 * d) * b + (a2 * d) * f)
        exprC2FBBothLeft = changeOrderFirst exprC1FBDeleted

        exprC2FBDeleted : (a1 * d) * f + (e2 * d) * b =
                          (e1 * d) * b + (a2 * d) * f
        exprC2FBDeleted = plusLeftCancel _ ((a1 * d) * f + (e2 * d) * b) ((e1 * d) * b + (a2 * d) * f) exprC2FBBothLeft

        exprReorder : (a1 * d) * f + (e2 * d) * b = (a2 * d) * f + (e1 * d) * b
        exprReorder = rewrite sym $ plusCommutative ((e1 * d) * b) ((a2 * d) * f) in exprC2FBDeleted

        exprDFirst : d * (a1 * f + e2 * b) = d * (a2 * f + e1 * b)
        exprDFirst =
            rewrite multDistributesOverPlusRight (d) (a1 * f) (e2 * b) in
            rewrite multDistributesOverPlusRight (d) (a2 * f) (e1 * b) in
            rewrite lemma (d) a1 (f) in
            rewrite lemma (d) a2 (f) in
            rewrite lemma (d) e1 (b) in
            rewrite lemma (d) e2 (b) in
            exprReorder
            where
                lemma : (x : Nat) -> (y : Nat) -> (z : Nat) -> x * (y * z) = y * x * z
                lemma x y z =
                    rewrite multAssociative x y z in
                    rewrite multCommutative x y in
                    Refl

        res : a1 * f + e2 * b = e1 * b + a2 * f
        res = rewrite plusCommutative (e1 * b) (a2 * f) in multNZLeftCancel prfD exprDFirst

RatSetoid : Setoid
RatSetoid = MkSetoid MyRat EqRat $ MkEquality ratRefl ratSymm ratTrans
