module Setoid

%default total
%access public export

Relation : Type -> Type
Relation a = a -> a -> Type

RelationProperty : Type -> Type
RelationProperty a = Relation a -> Type

Trans : {A: Type} -> RelationProperty A
Trans {A=typeA} rel = (a: typeA) -> (b: typeA) -> (c: typeA) -> rel a b -> rel b c -> rel a c

Symm : {A: Type} -> RelationProperty A
Symm {A=typeA} rel = (a: typeA) -> (b: typeA) -> rel a b -> rel b a

Rfl : {A: Type} -> RelationProperty A
Rfl {A=typeA} rel = (a: typeA) -> rel a a

refl_eq : Rfl (=)
refl_eq _ = Refl

symm_eq : Symm (=)
symm_eq _ _ = sym

trans_eq : Trans (=)
trans_eq _ _  _ = trans

data Equality : {A: Type} -> RelationProperty A where
  MkEquality : {A: Type} -> {R: Relation A} -> Rfl R -> Symm R -> Trans R -> Equality R

record Setoid where
  constructor MkSetoid
  C: Type
  CEq: Relation C
  CEqProof: Equality CEq

IntensionalSetoid : Type -> Setoid
IntensionalSetoid t = MkSetoid t (=) $ MkEquality refl_eq symm_eq trans_eq
