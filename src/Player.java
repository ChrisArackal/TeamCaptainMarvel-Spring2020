import java.io.Serializable;
import java.util.ArrayList;
import java.util.Scanner;

public class Player extends Entity implements Serializable{

	//instance variables
	private ArrayList<Item> inventory;
	private ArrayList<Item> usedItems; //items used by player, checked for monster fights
	private int currentRoomID;

	//constructors
	public Player(String entityName, int health, int currentRoomID, ArrayList<Item> inventory, ArrayList<Item> usedItems) {

		super(entityName, health);
		this.currentRoomID = currentRoomID;
		this.inventory = inventory;
		this.usedItems = usedItems;
	}

	//getters and setters
	public ArrayList<Item> getInventory() {
		return inventory;
	}

	public void setInventory(ArrayList<Item> inventory) {
		this.inventory = inventory;
	}

	public int getCurrentRoomID() {
		return currentRoomID;
	}

	public void setCurrentRoomID(int currentRoomID) {
		this.currentRoomID = currentRoomID;
	}

	//method to print out players inventory
	public void inventory()
	{
		if(!this.getInventory().isEmpty())
		{
			System.out.println("These are the items in your inventory ->");
			for(int i = 0; i < this.getInventory().size(); i++)
			{
				System.out.println(this.getInventory().get(i).getItemName());
			}
		}
		else
		{
			System.out.println("Your inventory is empty.");
		}
	}

	//method to see if item is in player's inventory
	public boolean itemAvailable(String itemName) {

		boolean itemAvailable = false;

		if(this.getInventory().isEmpty())
		{
			itemAvailable = false;
		}
		else if (this.getInventory().size() > 0)
		{
			for(int i = 0; i < this.getInventory().size(); i++)
			{
				if(itemName.equalsIgnoreCase(this.getInventory().get(i).getItemName()))
				{
					itemAvailable = true;
					break;
				}
				else
				{
					itemAvailable = false;
				}
			}
		}
		return itemAvailable;
	}

	//method to search if there are any items in room
	public void search(Room room)
	{
		if(room.getItemInventory().isEmpty())
		{
			System.out.println("This room is empty. There are no items.");
		}
		else
		{
			for(int i = 0; i < room.getItemInventory().size(); i++)
			{
				System.out.println(room.getItemInventory().get(i).getItemName());
			}
		}
	}

	//method to display item description if the examine item command is used and the item is in the player's inventory
	public void inspect(String itemName, Room room) {
		String itemDescription = "";

		if(!this.itemAvailable(itemName) && !room.itemAvailable(itemName))
		{
			itemDescription = itemName + " is not available for examination";
		}
		else
		{
			if(room.itemAvailable(itemName))
			{
				for(int i = 0; i < room.getItemInventory().size(); i++)
				{
					if(itemName.equalsIgnoreCase(room.getItemInventory().get(i).getItemName()))
					{
						itemDescription = room.getItemInventory().get(i).getItemDescription();
						break;
					}
				}
			}
			else
			{
				for(int i = 0; i < this.getInventory().size(); i++)
				{
					if(itemName.equalsIgnoreCase(this.getInventory().get(i).getItemName()))
					{
						itemDescription = this.getInventory().get(i).getItemDescription();
						break;
					}
				}
			}
		}
		System.out.println(itemDescription);
	}

	//method that allows player to pick up method from room and add it to their inventory
	public void pickupItem(String itemName, Room room) {
		String itemPickedUpMessage = "";

		if(!room.isHasItem())
		{
			itemPickedUpMessage = itemPickedUpMessage + "This room does not have any items!";
		}
		else if(room.itemAvailable(itemName))
		{
			for(int i = 0; i < room.getItemInventory().size(); i++)
			{
				if(itemName.equalsIgnoreCase(room.getItemInventory().get(i).getItemName()))
				{
					Item item = room.getItemInventory().get(i);
					this.getInventory().add(item);
					room.getItemInventory().remove(i);
					if(room.getItemInventory().isEmpty())
					{
						room.setHasItem(false);
					}
					if(item instanceof Equipable)
					{
						
						itemPickedUpMessage = ((Equipable) item).getPickupMessage();

					}
					else
					{
						itemPickedUpMessage = itemName + " has been picked up from the room and successfully added to " +
								this.getEntityName() + "'s inventory.";
						
					}
					break;
				}
			}
		}
		else
		{
			itemPickedUpMessage = itemName + " is not in the current room.";
		}
		System.out.println(itemPickedUpMessage);
	}

	//method to drop item from the player's inventory into the current room's inventory
	public void dropItem(String itemName, Room room) {
		String itemDroppedMessage = "";

		if(this.inventory.isEmpty())
		{
			itemDroppedMessage = itemDroppedMessage + "You do not have any items in your inventory.";
		}
		else if(this.itemAvailable(itemName))
		{
			for(int i = 0; i < this.inventory.size(); i++)
			{
				if(itemName.equalsIgnoreCase(this.inventory.get(i).getItemName()))
				{
					Item item = this.inventory.get(i);
					room.getItemInventory().add(item);
					inventory.remove(i);
					room.setHasItem(true);
					itemDroppedMessage = itemName + " has been dropped successfully from " + this.getEntityName()
					+ "'s inventory and placed in the " + room.getRoomName() + ".";
				}
			}
		}
		else
		{
			itemDroppedMessage = itemDroppedMessage + itemName + " is not in your item inventory.";
		}
		System.out.println(itemDroppedMessage);
	}

	public void useItem(String itemName) {
		String useItemMessage = "";

		if(this.inventory.isEmpty())
		{
			useItemMessage = useItemMessage + "You do not have any items in your inventory.";
		}
		else if(this.itemAvailable(itemName))
		{
			Item item = searchItemByName(itemName);
			if(item instanceof Equipable)
			{
				for(int i = 0; i < this.inventory.size(); i++)
				{
					if(itemName.equalsIgnoreCase(this.inventory.get(i).getItemName()))
					{
						Equipable usedItem = (Equipable) this.inventory.get(i);
						inventory.remove(i);
						usedItems.add(usedItem);
						if(usedItem.getUseMessage() == null)
						{
							useItemMessage = useItemMessage + usedItem.getItemName() + " has been used successfully.";
						}
						else
						{
							useItemMessage = usedItem.getUseMessage();
						}
					}
				}
			}
			else
			{
				useItemMessage = useItemMessage + "This item is not useable. Must be a consumable.";
			}
		}
		else
		{
			useItemMessage = useItemMessage + itemName + " is not in your item inventory.";
		}
		System.out.println(useItemMessage);
	}

	//method to search item by name and return item from inventory
	private Item searchItemByName(String itemName) {
		Item item = null;

		for(int i = 0; i < inventory.size(); i++)
		{
			if(inventory.get(i).getItemName().equalsIgnoreCase(itemName))
			{
				item = inventory.get(i);
			}
		}

		return item;
	}

	public void consumeItem(String itemName) {
		if(itemAvailable(itemName))
		{
			Item item = searchItemByName(itemName);

			if(item instanceof Consumable)
			{
				int health = this.getHealth() + ((Consumable) item).getHealthIncrease();
				this.setHealth(health);
				inventory.remove(item);
				usedItems.add(item);
				System.out.println(itemName + " has been consumed and your health is now " + this.getHealth() + " health points.");	
			}
			else
			{
				System.out.println(itemName + " is not a consumable item.");
			}
		}
	}
	
	public boolean inUsedItems(int itemID) {
		boolean inUsedItems = false;

		if(usedItems.isEmpty())
		{
			inUsedItems = false;
		}
		else if (usedItems.size() > 0)
		{
			for(int i = 0; i < usedItems.size(); i++)
			{
				if(itemID == usedItems.get(i).getItemID())
				{
					inUsedItems = true;
					break;
				}
				else
				{
					inUsedItems = false;
				}
			}
		}
		return inUsedItems;
	}
	
	public void attack(Room room, Scanner kb) {
		
		if (room.getMonsters().isEmpty()) {
			System.out.println("No monsters around to attack");
			return;
		}
		
		Monster monster = room.getMonsters().get(0);
		
		int[] items = monster.getItemIDArray();
		int countOfItems = 0;
		
		for (int i: items) {
			if (inUsedItems(i)) {
				countOfItems++;
			}
		}
		
		boolean playerDamage = true; //true if player does damage to monster, false if monster does damage to player
		
		if (countOfItems == 0) {
			playerDamage = attackCalc(monster.getChanceOfWinning());
		}
		
		else if (countOfItems == 1) {
			playerDamage = attackCalc(monster.getIncreasedOddsArray()[0]);
		}
		
		else if (countOfItems == 2) {
			playerDamage = attackCalc(monster.getIncreasedOddsArray()[1]);
		}
		
		if (playerDamage) {
			monster.setHealth(monster.getHealth() - 1);
			System.out.println("You did 1 heart of damage to the " + monster.getEntityName() + ". It now has " + monster.getHealth() + " heart(s) left");
			if (monster.getHealth() == 0) {
				System.out.println(monster.getEntityName() + " has been defeated");
				room.getMonsters().remove(0);
				
				if(room.getMonsters().isEmpty()) {
					System.out.println("You defeated all the monsters here");
					return;
				}
			}
		}
		else {
			setHealth(getHealth() - 1);
			System.out.println("You took 1 heart of damage. You now have " + getHealth() + " heart(s) left");
			if (getHealth() == 0) {
				System.out.println("You have been defeated. Game Over");
				System.exit(0);
			}
		}
		
		System.out.println("Attack or Runaway");
		String choice = kb.nextLine();
		
		while (!(choice.equalsIgnoreCase("attack") || choice.equalsIgnoreCase("runaway"))) {
			System.out.println("You must attack again or runaway");
		}
		
		if (choice.equalsIgnoreCase("attack"))
			attack(room, kb);
		else
			return;
		
	}
	
	public boolean attackCalc(int playerChance) {
		
		int chance = (int)(Math.random() * 100 + 1);
		
		if (chance <= playerChance)
			return true;
		else
			return false;
		
		
	}
}
