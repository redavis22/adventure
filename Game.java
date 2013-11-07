import java.util.Scanner;
import java.util.LinkedList;

public class Game {

	protected Scanner scanner;
	protected PlayerInterpreter interpreter;
	protected Player player;
	
	public Game() {

		this(null);
	}
	public Game(java.io.File save) {
	
		// Parse room from file
		//Room startingRoom = Map.njit();
		Room startingRoom = Map.level1();
		//Room startingRoom = Map.skydiving();
		
		this.scanner = new Scanner(System.in);
		this.interpreter = new PlayerInterpreter();
		
		// Parse inventory from file
		LinkedList<Item> items = new LinkedList<Item>();
		if(items != null) {
			this.player = new Player(startingRoom, items);
		}
		else {
			this.player = new Player(startingRoom);
		}
	}

	public void start() throws NullPointerException {

		try {
		String input = "";
		while(input.compareTo("quit") != 0) {
			System.out.print(">>> ");
			input = this.scanner.nextLine();
			Action a = this.interpreter.interpretString(input);
			switch(a.type()) {
				case TYPE_DIRECTIONAL:
					move(a);
					break;
				case TYPE_HASDIRECTOBJECT:
					switch(a) {
						case ActionPickUp: {
							//this.player.pickup(a.directObject());
							Item o = a.directObject();
							if(this.player.currentRoom.hasItem(o)) {
								if(o instanceof Acquirable) {
									//pickup
									this.player.currentRoom.remove(o);
									this.player.pickup(o);
									System.out.println("Taken.");
								}
								else {
									System.out.println("You cannot pick up this item.");
								}
							}
							else if(this.player.hasItem(o)) {
								System.out.println("You already have that item in your inventory.");
							}
							else {
								System.out.println("I don't see that here.");
							}
							break;
						}
						case ActionBreak: {
							Item item = a.directObject();
							if(this.player.currentRoom.hasItem(item) || this.player.hasItem(item)) {
								if(item instanceof Destroyable) {
									((Destroyable)item).destroy();
									System.out.println("It has shattered into a million pieces.");
								}
								else {
									System.out.println("You cannot break this item.");
								}
							}
							else {
								System.out.println("I don't see that here.");
							}
							break;
						}
						case ActionInspect: {
							Item item = a.directObject();
							if(this.player.currentRoom.hasItem(item) || this.player.hasItem(item)) {
								if(item instanceof Inspectable) {
									((Inspectable)item).inspect();
								}
								else {
									System.out.println("You cannot inspect this item.");
								}
							}
							else {
								System.out.println("I don't see that here.");
							}
							break;
						}
						case ActionDrop: {
							Item item = a.directObject();
							if(this.player.hasItem(item)) {
								if(item instanceof Droppable) {
									((Droppable)item).drop();
									System.out.println("Dropped.");
								}
								else {
									System.out.println("You cannot drop this item.");
								}
							}
							else {
								System.out.println("You don't have that item to drop.");
							}
							break;
						}
						case ActionThrow: {
							Item item = a.directObject();
							if(this.player.hasItem(item)) {
								if(item instanceof Chuckable) {
									((Chuckable)item).chuck();
									System.out.println("Thrown.");
								}
								else {
									System.out.println("You cannot throw this item.");
								}
							}
							else {
								System.out.println("You don't have that item to throw.");
							}
							break;
						}
						case ActionShake: {
							this.player.die();
							break;
						}
						case ActionEnable: {
							Item item = a.directObject();
							if(this.player.currentRoom.hasItem(item) || this.player.hasItem(item)) {
								if(item instanceof Startable) {
									((Startable)item).start();
								}
								else {
									System.out.println("I don't know how to do that.");
								}
							}
							else {
								System.out.println("I don't see that here.");
							}
							break;
						}
						case ActionPush: {
							Item item = a.directObject();
							if(this.player.currentRoom.hasItem(item) || this.player.hasItem(item)) {
								if(item instanceof Pushable) {
									((Pushable)item).push();
								}
								else {
									System.out.println("Nothing happens.");
								}
							}
							else {
								System.out.println("I don't see that here.");
							}
							/*
							if(directObject == Item.ItemElevatorButton) {
								((RoomElevator)directObject.getElevator()).call(this.player.currentRoom);
							}
							else if(directObject.toString().equals("Elevator Button")){
								RoomElevator e = (RoomElevator)this.player.currentRoom;
								e.call(Integer.parseInt(directObject.getAliases()[0])-1);
								for(int i=0; i < 3; i++) {
									System.out.println("...");
									try {
										Thread.sleep(1000);
									} catch(Exception e1) {
										e1.printStackTrace();
									}
								}
								System.out.println("Ding");
								// TODO : add support for restricted floors
								if(floor is restricted) {
									"this floor is restricted"
									"the doors do not open"
								}
								this.player.look();
							}
							else if(directObject == Item.ItemFridge){
								if(this.player.currentRoom().hasItem(directObject)) {
									directObject.push();
									// set room to not obscured
									directObject.getPassage().setObscured(false);
								}
								else {
									System.out.println("I don't see that here");
								}
							}
							else {
								if(this.player.currentRoom().hasItem(directObject)) {
									directObject.push();
								}
								else {
									System.out.println("I don't see that here");
								}
							}
							*/
							break;
						}
						case ActionEat: {
							Item item = a.directObject();
							if(this.player.currentRoom.hasItem(item) || this.player.hasItem(item)) {
								if(item instanceof Edible) {
									((Edible)item).eat();
								}
								else {
									System.out.println("As you forcefully shove the " + a.directObject() + " down your throat, you begin to choke.");
									this.player.die();
								}
							}
							else {
								System.out.println("I don't see that here.");
							}
							break;
						}
					}
				case TYPE_HASINDIRECTOBJECT:
					switch(a) {
						case ActionPut: {
							Item itemToPut = a.directObject();
							Item itemToBePutInto = a.indirectObject();
							if(!this.player.hasItem(itemToPut)) {
								System.out.println("You don't have that object in your inventory.");
								break;
							}
							else if(!this.player.currentRoom.hasItem(itemToBePutInto)) {
								System.out.println("That object doesn't exist in this room.");
								break;
							}
							else if(!(itemToBePutInto instanceof Hostable) || !(itemToPut instanceof Installable)) {
								System.out.println("You cannot install a " + itemToPut + " into this " + itemToBePutInto);
							}
							else {
								this.player.drop(itemToPut);
								this.player.putItemInItem(itemToPut, itemToBePutInto);
								System.out.println("Done");
							/*
								if(itemToBePutInto == Item.ItemLock) {
									// this restricts me to one locked adjacent room at a time
									
									for(Action action : Action.values()) {
										
										Room adjacentRoom = this.player.currentRoom.getRoomForDirection(action);
										if(adjacentRoom instanceof RoomLockable) {
											((RoomLockable)adjacentRoom).unlock(itemToPut);
											break;
										}
									}
								}	
								System.out.println("Done");
							*/
							}
							break;
						}
						case ActionTake: {
							Item contents = a.directObject();
							Item container = a.indirectObject();
							if(!this.player.currentRoom.hasItem(container)) {
								System.out.println("I don't see that here.");
							}
							else if(!(container instanceof Hostable)) {
								System.out.println("You can't have an item inside that.");
							}		
							else {
								if(((Hostable)container).installedItem() == contents) {
									((Hostable)container).uninstall(contents);
									this.player.pickup(contents);
								}	
								else {
									System.out.println("That item is not inside this " + container);
								}
							}
							break;
						}
					}
				case TYPE_HASNOOBJECT: {
					switch(a) {
						case ActionLook:
							this.player.look();
							break;
						case ActionDig:
							if(this.player.currentRoom instanceof RoomExcavatable) {
								RoomExcavatable curr = (RoomExcavatable)this.player.currentRoom;
								curr.dig();
							}
							else {
								System.out.println("You are not allowed to dig here");
							}

							break;
						case ActionJump:
							move(Action.ActionGoDown);
							break;
						case ActionViewItems: 
							LinkedList<Item> items = this.player.getItems();
							if (items.size() == 0) {
								System.out.println("You don't have any items");
							}
							else {
								for(Item item : this.player.getItems()) {
									System.out.println("You have a " + item.detailDescription());
								}
							}
							break;
						case ActionSuicide:
							this.player.die();
							break;
						case ActionPass:
							// intentionally blank
							break;
						case ActionHelp:
							help();
							break;	
					}
					break;
					}
				default:
					System.out.println("I don't understand that");
					break; 
				}
			}
		}catch(Exception n) {
			System.out.println("I don't understand that (exception caught)");
			start();
		}

		System.out.println("Quitting game...");
	}
	private void move(Action a) {
	
		// test if room is dark
		if(this.player.currentRoom instanceof RoomDark) {
			if(((RoomDark)this.player.currentRoom).isDark() && ((RoomDark)this.player.currentRoom).willDieInDirection(a) && !this.player.hasItem(Item.getInstance("flashlight"))) {
				System.out.println("As you take your first step within the dark room, you trip on a mysterious object. You fall toward the floor, and hit your head against a large rock");
				this.player.die();
			}
		}
		if(this.player.currentRoom.canMoveToRoomInDirection(a)) {

			Room nextRoom = this.player.currentRoom.getRoomForDirection(a);
			// test if requires key
			if(nextRoom instanceof RoomLockable) {

				RoomLockable lockedRoom = (RoomLockable)nextRoom;
				if(lockedRoom.isLocked()) {

					if(lockedRoom.causesDeath()) {
						System.out.println(lockedRoom.deathMessage());
						this.player.die();
					}
					System.out.println("This door is locked. You must unlock it.");
					return;
				}
			}
			else if(nextRoom instanceof RoomObscured) {
				RoomObscured obscuredRoom = (RoomObscured)nextRoom;
				if(obscuredRoom.isObscured()) {
					System.out.println("You can't move that way");
					return;
				}
			}
			this.player.move(nextRoom);
			if(nextRoom instanceof RoomSky) {
				RoomSky sky = (RoomSky)nextRoom;
				sky.freefall();
			}
		}
		else {
			System.out.println("You can't move that way");
		}
	}
	private void help() {

		System.out.println(" -- Text RPG Help Menu -- ");
		
		System.out.println("To view your current items: type \"inventory\"");
		System.out.println("To travel in a direction like north, type \"Go North\"");
		System.out.println("You can inspect an inspectable item by typing \"Inspect <item>\"");
	}
}
