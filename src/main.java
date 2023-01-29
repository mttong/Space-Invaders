import tester.*;
import java.util.Random;
import javalib.worldimages.*;
import javalib.funworld.*;
import java.awt.Color;

class CartPt {
  int x;
  int y;

  CartPt(int x, int y) {
    this.x = new Util().checkVal(x);
    this.y = new Util().checkVal(y);
  }

  /*
   * TEMPLATE FIELDS:
   * 
   * this.x -- int 
   * this.y -- int
   * 
   * METHODS:
   * 
   * this.samePoint(CartPT) -- boolean
   * 
   */

  // determines if two CartPts are the same
  boolean samePoint(CartPt that) {
    return this.x == that.x && this.y == that.y;
  }
}

class Invader {
  CartPt position;

  Invader(CartPt position) {
    this.position = position;
  }

  /*
   * TEMPLATE FIELDS: 
   * 
   * this.position -- CartPt
   * 
   * METHODS: 
   * 
   * this.draw() -- WorldImage 
   * this.placeOnto(WorldScene) -- WorldScene
   * this.inContact(IBullet) -- boolean 
   * this.makeBullet() -- IBullet
   * 
   */

  // draws individual invaders onto the WorldScene
  WorldImage draw() {
    return new ScaleImage(new FromFileImage("invader.png"), 0.075);
  }

  // place the invader onto the given scene
  WorldScene placeOnto(WorldScene scene) {
    return scene.placeImageXY(this.draw(), this.position.x, this.position.y);
  }

  // does the bullet come incontact with this invader?
  boolean inContact(IBullet bullet) {
    // pinhole does not lie at the top left, so we have to adjust the bounding box
    return bullet.within(this.position.x - 23, this.position.y - 20, this.position.x + 23,
        this.position.y + 20);
  }

  // make an invaderBullet at this invader's location
  IBullet makeBullet() {
    return new InvaderBullet(this.position);
  }
}

class Player {
  CartPt position;
  boolean isRight;

  // Constructor
  Player(CartPt position, boolean isRight) {
    this.position = position;
    this.isRight = isRight;
  }

  // Constructor keeping Player at constant y value
  Player(int x, boolean isRight) {
    this.position = new CartPt(x, 550);
    this.isRight = isRight;
  }

  // Constructor setting at start position
  Player() {
    this.position = new CartPt(300, 550);
    this.isRight = true;
  }

  /*
   * TEMPLATE FIELDS:
   * 
   * this.position -- CartPt 
   * this.isRight -- boolean
   * 
   * METHODS: 
   * 
   * this.draw() -- WorldScene 
   * this.placeOnto(WorldScene) -- WorldScene
   * this.move() -- Player 
   * this.makeBullet() -- PlayerBullet
   * this.changeDir(boolean) -- Player
   * 
   */

  // draws the player onto the WorldScene
  WorldImage draw() {
    return new RectangleImage(30, 20, OutlineMode.SOLID, Color.black);
  }

  // create a new player that is like this Player but is being moved in the x
  // direction
  Player move() {
    if (this.isRight) {
      if (this.position.x > 597) {
        return new Player(this.position.x - 3, !this.isRight); // reverse direction if at right edge
      }
      else {
        return new Player(this.position.x + 3, this.isRight);
      }
    }
    else {
      if (this.position.x < 3) {
        return new Player(this.position.x + 3, !this.isRight); // reverse direction if at left edge
      }
      else {
        return new Player(this.position.x - 3, this.isRight);
      }
    }
  }

  // makes bullet at Player's position
  PlayerBullet makeBullet() {
    return new PlayerBullet(this.position);
  }

  // place the player onto the given scene
  WorldScene placeOnto(WorldScene scene) {
    return scene.placeImageXY(this.draw(), this.position.x, this.position.y);
  }

  // change (or don't) this player's direction based on the boolean given
  Player changeDir(boolean right) {
    return new Player(this.position.x, right);
  }

  // does the bullet come incontact with this invader?
  boolean inContact(IBullet bullet) {
    // pinhole does not lie at the top left, so we have to adjust the bounding box
    return bullet.within(this.position.x - 15, this.position.y - 10, this.position.x + 15,
        this.position.y + 10);
  }
}

interface IBullet {
  // draws bullets onto Worldscene
  WorldImage draw();

  // create a new IBullet that is like this IBullet but is being moved in the y
  // direction
  IBullet move();

  // place the image of the bullet onto the given scene
  WorldScene placeOnto(WorldScene scene);

  // is the bullet close to the vertical edges?
  boolean leavingBounds();

  // does the bullet lie within the box bounded by the given x and y values?
  boolean within(int minX, int minY, int maxX, int maxY);
}

abstract class ABullet implements IBullet {
  CartPt position;
  Color color;
  boolean isPBullet; // did this come from the player?

  ABullet(CartPt position, Color color, boolean isPBullet) {
    this.position = position;
    this.color = color;
  }

  /*
   * TEMPLATE FIELDS: 
   * 
   * this.position -- CartPt 
   * this.color -- Color 
   * this.isPBullet -- boolean
   * 
   * METHODS: 
   * 
   * this.draw() -- WorldImage 
   * this.placeOnto(WorldScene) -- WorldScene
   * this.leavingBounds() -- boolean
   */

  // draws individual bullets
  public WorldImage draw() {
    return new CircleImage(2, "solid", this.color);
  }

  public abstract IBullet move();

  // place the bullet onto the given scene
  public WorldScene placeOnto(WorldScene scene) {
    return scene.placeImageXY(this.draw(), this.position.x, this.position.y);
  }

  // does the bullet lie on the vertical edges?
  // y = 600, 599, 1, 0
  public boolean leavingBounds() {
    return this.position.y >= 599 || this.position.y <= 1;
  }

  public boolean within(int minX, int minY, int maxX, int maxY) {
    return this.position.x >= minX && this.position.y >= minY && this.position.x <= maxX
        && this.position.y <= maxY;
  }
}

class InvaderBullet extends ABullet {

  InvaderBullet(CartPt position) {
    super(position, Color.red, false);
  }

  /*
   * TEMPLATE FIELDS: 
   * 
   * this.position -- CartPt 
   * this.color -- Color 
   * this.isPBullet -- boolean
   * 
   * 
   * 
   * METHODS: 
   * 
   * this.draw() -- WorldImage 
   * this.placeOnto(WorldScene) -- WorldScene
   * this.leavingBounds() -- boolean 
   * this.move() -- IBullet
   * 
   * METHODS ON FIELDS: 
   * 
   * this.position.samePoint(CartPt) -- CartPt
   */

  // move the invader bullet down
  public IBullet move() {
    return new InvaderBullet(new CartPt(this.position.x, this.position.y + 5));
  }
}

class PlayerBullet extends ABullet {

  PlayerBullet(CartPt position) {
    super(position, Color.black, true);
  }

  PlayerBullet(CartPt position, Color c, boolean isPBullet) {
    super(position, c, isPBullet);
  }

  /*
   * TEMPLATE FIELDS: 
   * 
   * this.position -- CartPt 
   * this.color -- Color 
   * this.isPBullet -- boolean
   * 
   * 
   * 
   * METHODS: 
   * 
   * this.draw() -- WorldImage 
   * this.placeOnto(WorldScene) -- WorldScene
   * this.leavingBounds() -- boolean 
   * this.move() -- IBullet
   * 
   * METHODS ON FIELDS: 
   * 
   * this.position.samePoint(CartPt) -- CartPt
   */

  // move the player bullets up
  public IBullet move() {
    return new PlayerBullet(new CartPt(this.position.x, this.position.y - 5));
  }
}

//place the bullets onto the given worldscene -- to be used with fold
class PlaceBullets implements IFunc2<IBullet, WorldScene, WorldScene> {
  public WorldScene apply(IBullet bullet, WorldScene acc) {
    return bullet.placeOnto(acc);
  }
}

//put the invader onto the given worldscene -- to be used with fold
class PlaceInvaders implements IFunc2<Invader, WorldScene, WorldScene> {
  public WorldScene apply(Invader invader, WorldScene acc) {
    return invader.placeOnto(acc);
  }
}

//make invader function, to be used with buildList
class MakeInvader implements IFunc<Integer, Invader> {
  public Invader apply(Integer num) {
    return new Invader(new CartPt(55 + 60 * (num % 9), 20 + 50 * (num / 9)));
  }
}

//is the point a member of the given list of points?
class InPoints implements IPred<CartPt> {
  IList<CartPt> points;

  InPoints(IList<CartPt> points) {
    this.points = points;
  }

  public boolean test(CartPt point) {
    return points.ormap(new ComparePoints(point));
  }
}

//are the two points the same?
class ComparePoints implements IPred<CartPt> {
  CartPt point;

  ComparePoints(CartPt point) {
    this.point = point;
  }

  public boolean test(CartPt other) {
    return point.samePoint(other);
  }
}

//move all of the bullets either up or down
class MoveBullets implements IFunc<IBullet, IBullet> {
  public IBullet apply(IBullet bullet) {
    return bullet.move();
  }
}

//is the bullet leaving the screen?
class InBounds implements IPred<IBullet> {
  public boolean test(IBullet bullet) {
    return !bullet.leavingBounds();
  }
}

//hit detection goes below

//for bullets:
//predicate<IBullet> that takes a list of invader when constructed
//and returns true if the bullet has come in contact with any of them
class HitNoInvaders implements IPred<IBullet> {
  IList<Invader> invaders;

  HitNoInvaders(IList<Invader> invaders) {
    this.invaders = invaders;
  }

  // want none of the invaders to be hit
  public boolean test(IBullet bullet) {
    return !invaders.ormap(new HitBullet(bullet));
  }
}

//predicate<Invader> takes in a bullet when constructed
//returns true if the invader is touching the bullet
class HitBullet implements IPred<Invader> {
  IBullet bullet;

  HitBullet(IBullet bullet) {
    this.bullet = bullet;
  }

  public boolean test(Invader invader) {
    return invader.inContact(bullet);
  }
}

//for invaders:
//predicate<Invader>  that takes a list of bullets when constructed
//does the invader come in contact with any of the bullets?
class HitNoBullets implements IPred<Invader> {
  IList<IBullet> bullets;

  HitNoBullets(IList<IBullet> bullets) {
    this.bullets = bullets;
  }

  public boolean test(Invader invader) {
    return !bullets.ormap(new HitInvader(invader));
  }
}

//predicate<IBullet> that takes in an invader and
//determines if the two are touching
class HitInvader implements IPred<IBullet> {
  Invader invader;

  HitInvader(Invader invader) {
    this.invader = invader;
  }

  public boolean test(IBullet bullet) {
    return invader.inContact(bullet);
  }
}

//invaders shooting below:
//IFunc2 that takes an invader, randomizes if it should shoot
//and builds a list of InvaderBullets 
//takes in a Random for testing
class InvaderShoot implements IFunc2<Invader, IList<IBullet>, IList<IBullet>> {
  Random rand;

  InvaderShoot(Random rand) {
    this.rand = rand;
  }

  InvaderShoot() {
    this(new Random());
  }

  public IList<IBullet> apply(Invader invader, IList<IBullet> acc) {
    if (acc.length() == 10) {
      return acc;
    }
    else if (rand.nextInt(100) < 10) {
      return new ConsList<IBullet>(invader.makeBullet(), acc);
    }
    else {
      return acc;
    }
  }
}

//hit detection for invaderBullet hitting the player 
class HitPlayer implements IPred<IBullet> {
  Player player;

  HitPlayer(Player player) {
    this.player = player;
  }

  public boolean test(IBullet invBullet) {
    return player.inContact(invBullet);
  }
}

class SpaceInvaders extends World {
  IList<Invader> invaders;
  IList<IBullet> pBullets;
  IList<IBullet> iBullets;
  Player player;

  SpaceInvaders(IList<Invader> invaders, IList<IBullet> pBullets, IList<IBullet> iBullets,
      Player player) {
    this.invaders = invaders;
    this.pBullets = pBullets;
    this.iBullets = iBullets;
    this.player = player;
  }

  // draw the state of the world
  public WorldScene makeScene() {
    return player.placeOnto(pBullets.fold(new PlaceBullets(), iBullets.fold(new PlaceBullets(),
        invaders.fold(new PlaceInvaders(), new WorldScene(600, 600)))));
  }

  // onTick()
  // move that which moves and detect hits
  public World onTick() {
    IList<IBullet> tempBullets = pBullets.filter(new HitNoInvaders(invaders));
    IList<Invader> tempInvader = invaders.filter(new HitNoBullets(pBullets));
    IList<IBullet> tempInvaderBullets = invaders.fold(new InvaderShoot(), iBullets);
    return new SpaceInvaders(tempInvader,
        // move all player bullets, check that they're on screen, then filter if they
        // hit an invader
        tempBullets.map(new MoveBullets()).filter(new InBounds()),
        tempInvaderBullets.map(new MoveBullets()).filter(new InBounds()), player.move());
  }

  // new method with random input for testing purposes
  public World onTick(Random rand) {
    IList<IBullet> tempBullets = pBullets.filter(new HitNoInvaders(invaders));
    IList<Invader> tempInvader = invaders.filter(new HitNoBullets(pBullets));
    IList<IBullet> tempInvaderBullets = invaders.fold(new InvaderShoot(rand), iBullets);
    return new SpaceInvaders(tempInvader,
        // move all player bullets, check that they're on screen, then filter if they
        // hit an invader
        tempBullets.map(new MoveBullets()).filter(new InBounds()),
        tempInvaderBullets.map(new MoveBullets()).filter(new InBounds()), player.move());
  }

  // onKeyEvent()
  // if right, set the the player's isRight to true
  // if left, set player's isRight to false
  // if space, shoot bullet
  public World onKeyEvent(String key) {
    if (key.equals(" ") && pBullets.length() < 3) {
      PlayerBullet add = player.makeBullet();
      return new SpaceInvaders(invaders, new ConsList<IBullet>(add, pBullets), iBullets, player);
    }
    else if (key.equals("right") || key.equals("left")) {
      return new SpaceInvaders(invaders, pBullets, iBullets, player.changeDir(key.equals("right")));
    }
    else {
      return this;
    }
  }

  // end the world
  public WorldEnd worldEnds() {
    if (iBullets.ormap(new HitPlayer(player))) {
      return new WorldEnd(true, this.invadersWinScene());
    }
    else if (invaders.length() == 0) {
      return new WorldEnd(true, this.playerWinScene());
    }
    else {
      return new WorldEnd(false, this.makeScene());
    }
  }

  // invaders win scene
  public WorldScene invadersWinScene() {
    return new WorldScene(600, 600).placeImageXY(new FromFileImage("youDied.png"), 300, 300);
  }

  // player win scene
  public WorldScene playerWinScene() {
    return new WorldScene(600, 600).placeImageXY(new FromFileImage("youWin.png"), 300, 300);
  }
}

class ExamplesSpaceInvaders {
  Player player = new Player();
  IList<Invader> invaderList = new Util().buildList(35, new MakeInvader());
  IList<IBullet> invaderBullets = new MtList<IBullet>();
  IList<IBullet> playerBullets = new MtList<IBullet>();
  SpaceInvaders defaultWorld = new SpaceInvaders(invaderList, playerBullets, invaderBullets,
      player);

  WorldScene background = new WorldScene(600, 600);
  WorldImage invaderImage = new ScaleImage(new FromFileImage("invader.png"), 0.075);
  WorldImage playerImage = new RectangleImage(30, 20, OutlineMode.SOLID, Color.black);
  WorldImage pBImage = new CircleImage(2, "solid", Color.black);
  WorldImage iBImage = new CircleImage(2, "solid", Color.red);

  ///////////////////////// testing examples
  ///////////////////////// /////////////////////////////////////////
  Invader invaderExample = new Invader(new CartPt(500, 100));
  IBullet playerBulletExample = new PlayerBullet(new CartPt(30, 30));
  IBullet invaderBulletExample = new InvaderBullet(new CartPt(225, 350));
  Player playerExample = new Player(100, true);
  Player playerGotHit = new Player(new CartPt(30, 90), true);

  IList<Invader> invadersExamples = new ConsList<Invader>(new Invader(new CartPt(305, 420)),
      new ConsList<Invader>(new Invader(new CartPt(200, 100)), new MtList<Invader>()));
  IList<IBullet> invBullets = new ConsList<IBullet>(new InvaderBullet(new CartPt(50, 50)),
      new MtList<IBullet>());
  IList<IBullet> playBullets = new ConsList<IBullet>(new PlayerBullet(new CartPt(150, 200)),
      new ConsList<IBullet>(new PlayerBullet(new CartPt(25, 10)),
          new ConsList<IBullet>(new PlayerBullet(new CartPt(567, 390)), new MtList<IBullet>())));

  WorldScene invaderAndPlayer = background.placeImageXY(this.invaderImage, 500, 100)
      .placeImageXY(playerImage, 300, 550);

  WorldScene justInvader = background.placeImageXY(this.invaderImage, 500, 100);

  WorldScene fullTestScene = background.placeImageXY(invaderImage, 305, 420)
      .placeImageXY(invaderImage, 200, 100).placeImageXY(iBImage, 50, 50)
      .placeImageXY(pBImage, 150, 200).placeImageXY(pBImage, 25, 10).placeImageXY(pBImage, 567, 390)
      .placeImageXY(playerImage, 100, 550);

  IList<CartPt> loCartPt = new ConsList<CartPt>(new CartPt(5, 6), new ConsList<CartPt>(
      new CartPt(65, 78), new ConsList<CartPt>(new CartPt(96, 591), new MtList<CartPt>())));

  IList<Invader> invaderExamples2 = new ConsList<Invader>(invaderExample, new MtList<Invader>());

  IList<IBullet> invBullets2 = new ConsList<IBullet>(new InvaderBullet(new CartPt(300, 300)),
      new ConsList<IBullet>(new InvaderBullet(new CartPt(30, 90)),
          new ConsList<IBullet>(new InvaderBullet(new CartPt(25, 60)),
              new ConsList<IBullet>(new InvaderBullet(new CartPt(400, 529)),
                  new ConsList<IBullet>(new InvaderBullet(new CartPt(200, 300)),
                      new ConsList<IBullet>(new InvaderBullet(new CartPt(423, 123)),
                          new ConsList<IBullet>(new InvaderBullet(new CartPt(412, 521)),
                              new ConsList<IBullet>(new InvaderBullet(new CartPt(63, 25)),
                                  new ConsList<IBullet>(new InvaderBullet(new CartPt(213, 143)),
                                      new ConsList<IBullet>(new InvaderBullet(new CartPt(234, 523)),
                                          new MtList<IBullet>()))))))))));

  IList<IBullet> invBullets2Moved = new ConsList<IBullet>(new InvaderBullet(new CartPt(300, 305)),
      new ConsList<IBullet>(new InvaderBullet(new CartPt(30, 95)),
          new ConsList<IBullet>(new InvaderBullet(new CartPt(25, 65)),
              new ConsList<IBullet>(new InvaderBullet(new CartPt(400, 534)),
                  new ConsList<IBullet>(new InvaderBullet(new CartPt(200, 305)),
                      new ConsList<IBullet>(new InvaderBullet(new CartPt(423, 128)),
                          new ConsList<IBullet>(new InvaderBullet(new CartPt(412, 526)),
                              new ConsList<IBullet>(new InvaderBullet(new CartPt(63, 30)),
                                  new ConsList<IBullet>(new InvaderBullet(new CartPt(213, 148)),
                                      new ConsList<IBullet>(new InvaderBullet(new CartPt(234, 528)),
                                          new MtList<IBullet>()))))))))));

  IList<Invader> invaderListEmpty = new MtList<Invader>();

  // random seed
  Random rand910 = new Random(910);

  //////////////////////// testing methods in object
  //////////////////////// classes///////////////////////////////////////////////

  // testing CartPt Constructor Exceptions
  boolean testValidInput(Tester t) {
    return t.checkConstructorException(new IllegalArgumentException("Argument out of bounds!"),
        "CartPt", 601, 300)
        && t.checkConstructorException(new IllegalArgumentException("Argument out of bounds!"),
            "CartPt", 800, -10)
        && t.checkConstructorException(new IllegalArgumentException("Argument out of bounds!"),
            "Player", -100, true)
        && t.checkConstructorException(new IllegalArgumentException("Argument out of bounds!"),
            "CartPt", 300, 900);
  }

  // testing samePoint in CartPt
  boolean testSamePoint(Tester t) {
    return t.checkExpect(new CartPt(58, 102).samePoint(new CartPt(324, 123)), false)
        && t.checkExpect(new CartPt(324, 123).samePoint(new CartPt(324, 123)), true);
  }

  // testing draw
  boolean testDraw(Tester t) {
    return t.checkExpect(invaderExample.draw(), this.invaderImage)
        && t.checkExpect(player.draw(), this.playerImage)
        && t.checkExpect(playerBulletExample.draw(), new CircleImage(2, "solid", Color.black))
        && t.checkExpect(invaderBulletExample.draw(), new CircleImage(2, "solid", Color.red));
  }

  // testing placeOnto
  boolean testPlaceOnto(Tester t) {
    return t.checkExpect(invaderExample.placeOnto(background), justInvader)
        && t.checkExpect(player.placeOnto(justInvader), invaderAndPlayer)
        && t.checkExpect(playerBulletExample.placeOnto(background),
            background.placeImageXY(pBImage, 30, 30))
        && t.checkExpect(invaderBulletExample.placeOnto(invaderAndPlayer),
            invaderAndPlayer.placeImageXY(iBImage, 225, 350));
  }

  // testing inContact in Invader and Player 
  boolean testInContact(Tester t) {
    return t.checkExpect(invaderExample.inContact(new PlayerBullet(new CartPt(523, 120))), true)
        && t.checkExpect(
            new Invader(new CartPt(30, 45)).inContact(new PlayerBullet(new CartPt(7, 25))), true)
        && t.checkExpect(
            new Invader(new CartPt(45, 45)).inContact(new PlayerBullet(new CartPt(40, 40))), true)
        && t.checkExpect(
            new Invader(new CartPt(45, 45)).inContact(new PlayerBullet(new CartPt(100, 230))),
            false)
        && t.checkExpect(
            new Invader(new CartPt(45, 45)).inContact(new PlayerBullet(new CartPt(0, 0))), false)
        && t.checkExpect(playerExample.inContact(invaderBulletExample), false)
        && t.checkExpect(
            new Player(new CartPt(30, 45), true).inContact(new InvaderBullet(new CartPt(7, 25))),
            false)
        && t.checkExpect(
            new Player(new CartPt(45, 45), true).inContact(new InvaderBullet(new CartPt(40, 40))),
            true)
        && t.checkExpect(new Player(new CartPt(45, 45), false)
            .inContact(new InvaderBullet(new CartPt(100, 230))), false)
        && t.checkExpect(
            new Player(new CartPt(45, 45), false).inContact(new InvaderBullet(new CartPt(0, 0))),
            false);
  }

  // testing makeBullet in Invader and Player
  boolean testMakeBullet(Tester t) {
    return t.checkExpect(invaderExample.makeBullet(), new InvaderBullet(new CartPt(500, 100)))
        && t.checkExpect(new Invader(new CartPt(100, 200)).makeBullet(),
            new InvaderBullet(new CartPt(100, 200)))
        && t.checkExpect(new Player(new CartPt(300, 100), true).makeBullet(),
            new PlayerBullet(new CartPt(300, 100)))
        && t.checkExpect(player.makeBullet(), new PlayerBullet(new CartPt(300, 550)));
  }

  // testing move in Player and IBullet
  boolean testMove(Tester t) {
    return t.checkExpect(new PlayerBullet(new CartPt(300, 550)).move(),
        new PlayerBullet(new CartPt(300, 545)))
        && t.checkExpect(new PlayerBullet(new CartPt(0, 400)).move(),
            new PlayerBullet(new CartPt(0, 395)))
        && t.checkExpect(new InvaderBullet(new CartPt(456, 123)).move(),
            new InvaderBullet(new CartPt(456, 128)))
        && t.checkExpect(new InvaderBullet(new CartPt(500, 100)).move(),
            new InvaderBullet(new CartPt(500, 105)))
        && t.checkExpect(player.move(), new Player(303, true))
        && t.checkExpect(new Player(599, true).move(), new Player(596, false))
        && t.checkExpect(new Player(300, false).move(), new Player(297, false))
        && t.checkExpect(new Player(2, false).move(), new Player(5, true));
  }

  // testing changeDir in Player
  boolean testChangeDir(Tester t) {
    return t.checkExpect(playerExample.changeDir(true), new Player(100, true))
        && t.checkExpect(new Player(400, false).changeDir(true), new Player(400, true))
        && t.checkExpect(new Player(300, true).changeDir(false), new Player(300, false))
        && t.checkExpect(new Player(150, false).changeDir(false), new Player(150, false));
  }

  // testing leavingBounds in IBullet -- what is topLeft corner coordinates??
  boolean testLeavingBounds(Tester t) {
    return t.checkExpect(playerBulletExample.leavingBounds(), false)
        && t.checkExpect(invaderBulletExample.leavingBounds(), false)
        && t.checkExpect(new PlayerBullet(new CartPt(100, 1)).leavingBounds(), true)
        && t.checkExpect(new InvaderBullet(new CartPt(20, 599)).leavingBounds(), true);
  }

  // testing within in IBullet
  boolean testWithin(Tester t) {
    return t.checkExpect(playerBulletExample.within(20, 20, 40, 40), true)
        && t.checkExpect(invaderBulletExample.within(200, 200, 400, 400), true)
        && t.checkExpect(playerBulletExample.within(35, 35, 36, 36), false)
        && t.checkExpect(playerBulletExample.within(30, 30, 30, 45), true)
        && t.checkExpect(invaderBulletExample.within(10, 20, 225, 350), true);
  }

  ///////////////// testing function
  ///////////////// objects///////////////////////////////////////////

  // testing PlaceBullets[func2] function class
  boolean testPlaceBullets(Tester t) {
    return t.checkExpect(new PlaceBullets().apply(playerBulletExample, background),
        background.placeImageXY(new CircleImage(2, OutlineMode.SOLID, Color.black), 30, 30))
        && t.checkExpect(new PlaceBullets().apply(invaderBulletExample, background),
            background.placeImageXY(new CircleImage(2, OutlineMode.SOLID, Color.red), 225, 350))
        && t.checkExpect(
            new PlaceBullets().apply(new PlayerBullet(new CartPt(500, 100)), invaderAndPlayer),
            invaderAndPlayer.placeImageXY(new CircleImage(2, OutlineMode.SOLID, Color.black), 500,
                100));
  }

  // testing PlaceInvaders[func2] function class
  boolean testPlaceInvaders(Tester t) {
    return t.checkExpect(new PlaceInvaders().apply(invaderExample, background),
        background.placeImageXY(invaderImage, 500, 100))
        && t.checkExpect(
            new PlaceInvaders().apply(new Invader(new CartPt(300, 300)), invaderAndPlayer),
            invaderAndPlayer.placeImageXY(invaderImage, 300, 300));
  }

  // testing MakeInvader[buildList] function class
  boolean testMakeInvader(Tester t) {
    return t.checkExpect(new MakeInvader().apply(0), new Invader(new CartPt(55, 20)))
        && t.checkExpect(new MakeInvader().apply(9), new Invader(new CartPt(55, 70)))
        && t.checkExpect(new MakeInvader().apply(13), new Invader(new CartPt(295, 70)))
        && t.checkExpect(new MakeInvader().apply(22), new Invader(new CartPt(295, 120)));
  }

  // testing InPoints[pred] function class
  boolean testInPoints(Tester t) {
    return t.checkExpect(new InPoints(loCartPt).test(new CartPt(65, 78)), true)
        && t.checkExpect(new InPoints(loCartPt).test(new CartPt(200, 500)), false);
  }

  // testing ComparePoints[pred] function class
  boolean testComparePoints(Tester t) {
    return t.checkExpect(new ComparePoints(new CartPt(100, 100)).test(new CartPt(100, 100)), true)
        && t.checkExpect(new ComparePoints(new CartPt(101, 100)).test(new CartPt(100, 100)), false);
  }

  // testing MoveBullets[func] function class
  boolean testMoveBullets(Tester t) {
    return t.checkExpect(new MoveBullets().apply(invaderBulletExample),
        new InvaderBullet(new CartPt(225, 355)))
        && t.checkExpect(new MoveBullets().apply(playerBulletExample),
            new PlayerBullet(new CartPt(30, 25)))
        && t.checkExpect(new MoveBullets().apply(new InvaderBullet(new CartPt(105, 200))),
            new InvaderBullet(new CartPt(105, 205)))
        && t.checkExpect(new MoveBullets().apply(new PlayerBullet(new CartPt(20, 40))),
            new PlayerBullet(new CartPt(20, 35)));
  }

  // testing InBounds[pred] function class
  boolean testInBounds(Tester t) {
    return t.checkExpect(new InBounds().test(invaderBulletExample), true)
        && t.checkExpect(new InBounds().test(playerBulletExample), true)
        && t.checkExpect(new InBounds().test(new InvaderBullet(new CartPt(100, 599))), false)
        && t.checkExpect(new InBounds().test(new PlayerBullet(new CartPt(200, 1))), false);
  }

  // testing HitBullet[pred] function class
  boolean testHitBullet(Tester t) {
    return t.checkExpect(new HitBullet(new PlayerBullet(new CartPt(500, 100))).test(invaderExample),
        true)
        && t.checkExpect(new HitBullet(playerBulletExample).test(invaderExample), false)
        && t.checkExpect(new HitBullet(new PlayerBullet(new CartPt(477, 80))).test(invaderExample),
            true)
        && t.checkExpect(new HitBullet(new PlayerBullet(new CartPt(523, 120))).test(invaderExample),
            true)
        && t.checkExpect(new HitBullet(new PlayerBullet(new CartPt(523, 121))).test(invaderExample),
            false)
        && t.checkExpect(new HitBullet(new PlayerBullet(new CartPt(523, 119))).test(invaderExample),
            true);
  }

  // testing HitInvader[pred] function class
  boolean testHitInvader(Tester t) {
    return t.checkExpect(
        new HitInvader(invaderExample).test(new PlayerBullet(new CartPt(500, 100))), true)
        && t.checkExpect(new HitInvader(invaderExample).test(playerBulletExample), false)
        && t.checkExpect(new HitInvader(invaderExample).test(new PlayerBullet(new CartPt(477, 80))),
            true)
        && t.checkExpect(
            new HitInvader(invaderExample).test(new PlayerBullet(new CartPt(523, 120))), true)
        && t.checkExpect(
            new HitInvader(invaderExample).test(new PlayerBullet(new CartPt(523, 121))), false)
        && t.checkExpect(
            new HitInvader(invaderExample).test(new PlayerBullet(new CartPt(523, 119))), true);
  }

  // testing HitNoInvaders[pred] function class
  boolean testHitNoInvaders(Tester t) {
    return t.checkExpect(new HitNoInvaders(invaderList).test(playerBulletExample), true)
        && t.checkExpect(new HitNoInvaders(invaderList).test(new PlayerBullet(new CartPt(55, 20))),
            false)
        && t.checkExpect(new HitNoInvaders(invaderList).test(new PlayerBullet(new CartPt(32, 20))),
            false)
        && t.checkExpect(new HitNoInvaders(invaderList).test(new PlayerBullet(new CartPt(78, 20))),
            false)
        && t.checkExpect(new HitNoInvaders(invaderList).test(new PlayerBullet(new CartPt(55, 40))),
            false)
        && t.checkExpect(
            new HitNoInvaders(invaderList).test(new PlayerBullet(new CartPt(415, 120))), false)
        && t.checkExpect(new HitNoInvaders(invaderList).test(new PlayerBullet(new CartPt(22, 20))),
            true)
        && t.checkExpect(new HitNoInvaders(invaderList).test(new PlayerBullet(new CartPt(425, 20))),
            false)
        && t.checkExpect(
            new HitNoInvaders(invaderList).test(new PlayerBullet(new CartPt(449, 140))), true);
  }

  // testing HitNoBullets[pred] function class
  boolean testHitNoBullets(Tester t) {
    return t.checkExpect(new HitNoBullets(playBullets).test(invaderExample), true)
        && t.checkExpect(new HitNoBullets(playBullets).test(new Invader(new CartPt(150, 200))),
            false)
        && t.checkExpect(new HitNoBullets(playBullets).test(new Invader(new CartPt(567, 390))),
            false)
        && t.checkExpect(new HitNoBullets(playBullets).test(new Invader(new CartPt(127, 180))),
            false)
        && t.checkExpect(new HitNoBullets(playBullets).test(new Invader(new CartPt(173, 220))),
            false)
        && t.checkExpect(new HitNoBullets(playBullets).test(new Invader(new CartPt(177, 200))),
            true)
        && t.checkExpect(new HitNoBullets(playBullets).test(new Invader(new CartPt(150, 221))),
            true);
  }

  // testing InvaderShoot[IFunc2] function object
  boolean testInvaderShoot(Tester t) {
    return t.checkExpect(new InvaderShoot(rand910).apply(invaderExample, invaderBullets),
        new MtList<IBullet>())
        && t.checkExpect(new InvaderShoot(rand910).apply(invaderExample, playerBullets),
            new MtList<IBullet>())
        && t.checkExpect(new InvaderShoot(rand910).apply(invaderExample, invBullets),
            new ConsList<IBullet>(new InvaderBullet(new CartPt(50, 50)), new MtList<IBullet>()))
        && t.checkExpect(new InvaderShoot(rand910).apply(invaderExample, playBullets),
            new ConsList<IBullet>(new PlayerBullet(new CartPt(150, 200)),
                new ConsList<IBullet>(new PlayerBullet(new CartPt(25, 10)), new ConsList<IBullet>(
                    new PlayerBullet(new CartPt(567, 390)), new MtList<IBullet>()))))
        // if the length of IList<IBullet> accumulator is 10, then the function class
        // will
        // return the accumulator
        && t.checkExpect(new InvaderShoot(rand910).apply(invaderExample, invBullets2), invBullets2)
        && t.checkExpect(new InvaderShoot(rand910).apply(invaderExample, invBullets2Moved),
            invBullets2Moved);
  }

  // testing HitPlayer[pred] function object
  boolean testHitPlayer(Tester t) {
    return t.checkExpect(new HitPlayer(player).test(new InvaderBullet(new CartPt(300, 550))), true)
        && t.checkExpect(new HitPlayer(player).test(new InvaderBullet(new CartPt(285, 540))), true)
        && t.checkExpect(new HitPlayer(player).test(new InvaderBullet(new CartPt(315, 560))), true)
        && t.checkExpect(new HitPlayer(player).test(new InvaderBullet(new CartPt(316, 560))), false)
        && t.checkExpect(new HitPlayer(player).test(invaderBulletExample), false);
  }

  ////////////////////////////////////// testing worldstate
  ////////////////////////////////////// //////////////////////////////////////////

  // testing makeScene
  boolean testMakeScene(Tester t) {
    return t
        .checkExpect(new SpaceInvaders(new ConsList<Invader>(invaderExample, new MtList<Invader>()),
            playerBullets, invaderBullets, player).makeScene(), invaderAndPlayer)
        && t.checkExpect(
            new SpaceInvaders(invadersExamples, invBullets, playBullets, playerExample).makeScene(),
            fullTestScene);
  }

  // testing onTick
  boolean testOnTick(Tester t) {
    return t.checkExpect(
        new SpaceInvaders(new MtList<Invader>(), new MtList<IBullet>(), invBullets2, player)
            .onTick(),
        new SpaceInvaders(new MtList<Invader>(), new MtList<IBullet>(), invBullets2Moved,
            new Player(303, true)))
        && t.checkExpect(
            new SpaceInvaders(new MtList<Invader>(), playBullets, invBullets2, player).onTick(),
            new SpaceInvaders(new MtList<Invader>(),
                new ConsList<IBullet>(new PlayerBullet(new CartPt(150, 195)),
                    new ConsList<IBullet>(new PlayerBullet(new CartPt(25, 5)),
                        new ConsList<IBullet>(new PlayerBullet(new CartPt(567, 385)),
                            new MtList<IBullet>()))),
                invBullets2Moved, new Player(303, true)))
        && t.checkExpect(
            new SpaceInvaders(invaderExamples2, new MtList<IBullet>(), new MtList<IBullet>(),
                player).onTick(new Random(30)),
            new SpaceInvaders(invaderExamples2, new MtList<IBullet>(),
                new ConsList<IBullet>(new InvaderBullet(new CartPt(500, 105)),
                    new MtList<IBullet>()),
                new Player(303, true)))
        && t.checkExpect(
            new SpaceInvaders(invaderExamples2, new MtList<IBullet>(), new MtList<IBullet>(),
                player).onTick(new Random(10)),
            new SpaceInvaders(invaderExamples2, new MtList<IBullet>(), new MtList<IBullet>(),
                new Player(303, true)))
        && t.checkExpect(
            new SpaceInvaders(invadersExamples, new MtList<IBullet>(), new MtList<IBullet>(),
                player).onTick(new Random(30)),
            new SpaceInvaders(invadersExamples, new MtList<IBullet>(),
                new ConsList<IBullet>(new InvaderBullet(new CartPt(200, 105)),
                    new MtList<IBullet>()),
                new Player(303, true)))
        && t.checkExpect(
            new SpaceInvaders(invaderExamples2,
                new ConsList<IBullet>(new PlayerBullet(new CartPt(500, 100)),
                    new MtList<IBullet>()),
                invBullets2, player).onTick(new Random(1)),
            new SpaceInvaders(new MtList<Invader>(), new MtList<IBullet>(), invBullets2Moved,
                new Player(303, true)))
        && t.checkExpect(
            new SpaceInvaders(new MtList<Invader>(),
                new ConsList<IBullet>(new PlayerBullet(new CartPt(300, 6)), new MtList<IBullet>()),
                new MtList<IBullet>(), player).onTick(new Random(1)),
            new SpaceInvaders(new MtList<Invader>(), new MtList<IBullet>(), new MtList<IBullet>(),
                new Player(303, true)));
  }

  // testing onKeyEvent
  boolean testOnKeyEvent(Tester t) {
    return t.checkExpect(defaultWorld.onKeyEvent(" "), // player shoots
        new SpaceInvaders(invaderList,
            new ConsList<IBullet>(new PlayerBullet(new CartPt(300, 550)), playerBullets),
            invaderBullets, player))
        && t.checkExpect(defaultWorld.onKeyEvent("right"), defaultWorld) // already moving right
        && t.checkExpect(// player moving left then turning right
            new SpaceInvaders(invaderList, playerBullets, invaderBullets, new Player(300, false))
                .onKeyEvent("right"),
            defaultWorld)
        && t.checkExpect(defaultWorld.onKeyEvent("left"), // turning left
            new SpaceInvaders(invaderList, playerBullets, invaderBullets,
                new Player(new CartPt(300, 550), false)))
        && t.checkExpect(defaultWorld.onKeyEvent("t"), defaultWorld); // random key
  }

  // testing worldEnd
  boolean testWorldEnd(Tester t) {
    return t.checkExpect(defaultWorld.worldEnds(), new WorldEnd(false, defaultWorld.makeScene()))
        // invaders won, player lost result --one of the bullets in invBullets2 is the
        // same coordinates as playerGotHit
        && t.checkExpect(
            new SpaceInvaders(invaderList, playerBullets, invBullets2, playerGotHit).worldEnds(),
            new WorldEnd(true,
                new WorldScene(600, 600).placeImageXY(new FromFileImage("youDied.png"), 300, 300)))
        // player won, list of invaders is empty
        && t.checkExpect(
            new SpaceInvaders(invaderListEmpty, playerBullets, new MtList<IBullet>(), player)
                .worldEnds(),
            new WorldEnd(true,
                new WorldScene(600, 600).placeImageXY(new FromFileImage("youWin.png"), 300, 300)));
  }

  // testing invaderWinScene
  boolean testInvaderWinScene(Tester t) {
    return t.checkExpect(defaultWorld.invadersWinScene(),
        new WorldScene(600, 600).placeImageXY(new FromFileImage("youDied.png"), 300, 300));
  }

  // testing playerWinScene
  boolean testPlayerWinScene(Tester t) {
    return t.checkExpect(defaultWorld.playerWinScene(),
        new WorldScene(600, 600).placeImageXY(new FromFileImage("youWin.png"), 300, 300));
  }

  void testBigBang(Tester t) {
    SpaceInvaders world = defaultWorld;
    world.bigBang(600, 600, 0.05);
  }
  
  /*
     *CartPt class
  - samePoint   DONE
  Invader 
  - draw    DONE
  - placeOnto   DONE
  - inContact   DONE
  - makeBUllet  DONE
  Player
  - draw    DONE
  - move    DONE
  - makeBullet  DONE
  - placeOnto   DONE
  - changeDir   DONE
  - inContact   DONE
  IBullet - play and invader bullet 
  - draw          DONE
  - move --player and invader diff  DONE
  - placeOnto         DONE
  - leavingBounds         DONE
  - within          DONE
  function classes
   -placeBullets - func2    DONE
  - placeInvaders - func2   DONE
  - MakeINvader - buildlist   DONE
  - InPoints - pred     DONE
  - comparePoints - pred    DONE
  - moveBullets - func    DONE
  - InBounds - pred     DONE
  - InvaderShoot - func2 - random   DONE
  - HitPlayer - pred      DONE
  (hit detection)   
  - HitNoINvaders - pred      DONE
  - HitBullet - pred      DONE
  - HitNoBullets - pred     DONE
  - HitInvader - pred     DONE
  
  SpaceInvaders class (extends World) 
  - makeScene     DONE
  - onTick      DONE
  - onKeyEvent    DONE
  - WorldEnd      DONE
  - invaderWinScene   DONE
  - playerWinScene    DONE
  
  UTIL
  - buildList   DONE
  - checkVal    DONE
  - length    DONE
  */
}
