import tester.Tester;

interface IList<T> {
  // filter the items that don't satisfy the predicate
  IList<T> filter(IPred<T> p);

  // map the function onto the elements in the list
  <U> IList<U> map(IFunc<T, U> f);

  // fold the elements using the given function
  <U> U fold(IFunc2<T, U, U> func, U initial);

  // length of the list
  int length();

  // the ormap we all know and love
  boolean ormap(IPred<T> p);

}

class MtList<T> implements IList<T> {
  MtList() {
  }

  // filter for an empty list
  public IList<T> filter(IPred<T> p) {
    return new MtList<T>();
  }

  // map onto empty list is empty
  public <U> IList<U> map(IFunc<T, U> f) {
    return new MtList<U>();
  }

  public <U> U fold(IFunc2<T, U, U> func, U initial) {
    return initial;
  }

  public int length() {
    return 0;
  }

  public boolean ormap(IPred<T> p) {
    return false;
  }
}

class ConsList<T> implements IList<T> {
  T first;
  IList<T> rest;

  ConsList(T first, IList<T> rest) {
    this.first = first;
    this.rest = rest;
  }

  // filter the elements that don't satisfy the pred
  public IList<T> filter(IPred<T> p) {
    if (p.test(this.first)) {
      return new ConsList<T>(this.first, this.rest.filter(p));
    }
    else {
      return this.rest.filter(p);
    }
  }

  // map the function onto the element and recur
  public <U> IList<U> map(IFunc<T, U> f) {
    return new ConsList<U>(f.apply(this.first), this.rest.map(f));
  }

  // fold the list with the given function
  public <U> U fold(IFunc2<T, U, U> func, U initial) {
    return func.apply(this.first, this.rest.fold(func, initial));
  }

  public int length() {
    return 1 + this.rest.length();
  }

  public boolean ormap(IPred<T> p) {
    return p.test(first) || rest.ormap(p);
  }
}

interface IFunc<A, R> {
  R apply(A t);
}

interface IFunc2<A1, A2, R> {
  R apply(A1 i1, A2 i2);
}

interface IPred<A> {
  boolean test(A t);
}

class Util {
  <U> IList<U> buildList(Integer num, IFunc<Integer, U> func) {
    if (num == 0) {
      return new ConsList<U>(func.apply(num), new MtList<U>());
    }
    else {
      return new ConsList<U>(func.apply(num), buildList(num - 1, func));
    }
  }

  int checkVal(int val) {
    if (0 <= val && val <= 600) {
      return val;
    }
    else {
      throw new IllegalArgumentException("Argument out of bounds!");
    }
  }
}

//function classes for testing 

class Identity implements IFunc<Integer, Integer> {
  public Integer apply(Integer t) {
    return t;
  }
}

class One implements IFunc<Integer, Integer> {
  public Integer apply(Integer t) {
    return 1;
  }
}

class IsEven implements IPred<Integer> {
  public boolean test(Integer t) {
    return t % 2 == 0;
  }
}

class ExamplesUtils {
  IList<Integer> numList1 = new ConsList<Integer>(1,
      new ConsList<Integer>(2, new ConsList<Integer>(3, new MtList<Integer>())));
  IList<Integer> numList2 = new ConsList<Integer>(2,
      new ConsList<Integer>(3, new ConsList<Integer>(4, new MtList<Integer>())));
  IList<Integer> numList3 = new ConsList<Integer>(2,
      new ConsList<Integer>(1, new ConsList<Integer>(0, new MtList<Integer>())));
  IList<Integer> numList4 = new ConsList<Integer>(1,
      new ConsList<Integer>(1, new ConsList<Integer>(1, new MtList<Integer>())));

  boolean testBuildList(Tester t) {
    return t.checkExpect(new Util().buildList(2, new Identity()), numList3)
        && t.checkExpect(new Util().buildList(2, new One()), numList4);
  }

  boolean testOrmap(Tester t) {
    return t.checkExpect(numList1.ormap(new IsEven()), true)
        && t.checkExpect(numList4.ormap(new IsEven()), false);
  }
  
  boolean testLength(Tester t) {
    return t.checkExpect(numList1.length(), 3)
        && t.checkExpect(new MtList<Integer>().length(), 0)
        && t.checkExpect(numList2.length(), 3)
        && t.checkExpect(numList3.length(), 3)
        && t.checkExpect(numList4.length(), 3);
  }
}