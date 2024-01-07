import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.idlegame.dao.ItemDAO
import com.idlegame.objects.ActionMove
import com.idlegame.objects.Item
import java.util.concurrent.atomic.AtomicInteger

class InventoryDAO {
    private val db = FirebaseFirestore.getInstance()
    private val inventoryCollection = db.collection("inventory")

    private val defaultItemNames = listOf("Cigar", "Pack of Cigars", "Shoes", "Rainbow Shoes")
    private val defaultItems = mutableListOf<Item>()
    private val itemDAO = ItemDAO()
    private val userDAO = UserDAO()

    fun getItemsByUserId(userId: String, onComplete: (List<Item>, String?) -> Unit) {
        Log.d("InventoryDAO", "Fetching all items for userId: $userId")

        // First, fetch the email corresponding to the userId
        userDAO.getUserEmailByUserId(userId) { userEmail, userEmailError ->
            if (userEmailError != null || userEmail == null) {
                Log.e("InventoryDAO", "Error fetching user email: $userEmailError")
                onComplete(emptyList(), "Error fetching user email: $userEmailError")
            } else {
                // Now fetch the inventory items using the email
                inventoryCollection.whereEqualTo("email", userEmail).get()
                    .addOnSuccessListener { querySnapshot ->
                        if (!querySnapshot.isEmpty) {
                            Log.d("InventoryDAO", "Found inventory for email: $userEmail")
                            val allItems = mutableListOf<Item>()

                            querySnapshot.documents.firstOrNull()?.let { document ->
                                val inventoryData = document.data
                                Log.d("InventoryDAO", "Inventory data: $inventoryData")

                                val inventoryItems = inventoryData?.get("inventoryItems") as? List<Map<String, Any>>
                                inventoryItems?.forEach { itemData ->
                                    val item = mapItem(itemData)
                                    allItems.add(item)
                                }

                                onComplete(allItems, null)
                            }
                        } else {
                            Log.d("InventoryDAO", "Inventory not found for email: $userEmail")
                            onComplete(emptyList(), "Inventory not found for email: $userEmail")
                        }
                    }
                    .addOnFailureListener { e ->
                        Log.e("InventoryDAO", "Error fetching inventory for email: $userEmail, Error: ${e.message}")
                        onComplete(emptyList(), "Error fetching inventory: ${e.message}")
                    }
            }
        }
    }

    fun createInventory(userId: String, email: String, onComplete: (Boolean, String?) -> Unit) {
        Log.i("InventoryDAO", "Creating inventory for User ID: $userId, Email: $email")

        fetchDefaultItems { success, errorMessage ->
            if (success) {
                Log.i("InventoryDAO", "Successfully fetched default items for inventory.")

                val inventoryData = mapOf(
                    "email" to email,
                    "userId" to userId,
                    "inventoryItems" to defaultItems.map { item ->
                        mapOf(
                            "id" to item.id,
                            "name" to item.name,
                            "description" to item.description,
                            "actionMove" to mapOf(
                                "id" to item.actionMove.id,
                                "name" to item.actionMove.name,
                                "damage" to item.actionMove.damage,
                                "image" to item.actionMove.image
                            ),
                            "image" to item.image,
                            "isPicked" to true
                        )
                    }
                )

                inventoryCollection.document(email)
                    .set(inventoryData)
                    .addOnSuccessListener {
                        Log.i("InventoryDAO", "Inventory created successfully for User ID: $userId")
                        onComplete(true, null)
                    }
                    .addOnFailureListener { e ->
                        Log.e("InventoryDAO", "Error creating inventory for User ID: $userId, Error: ${e.message}")
                        onComplete(false, e.message)
                    }
            } else {
                Log.e("InventoryDAO", "Failed to fetch default items: $errorMessage")
                onComplete(false, errorMessage)
            }
        }
    }

    fun getPickedItemsByUserId(userId: String, onComplete: (List<Item>, String?) -> Unit) {
        Log.d("InventoryDAO", "Fetching picked items for userId: $userId")
        inventoryCollection.whereEqualTo("userId", userId).get()
            .addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    Log.d("InventoryDAO", "Found inventory for userId: $userId")
                    val pickedItems = mutableListOf<Item>()

                    querySnapshot.documents.firstOrNull()?.let { document ->
                        val inventoryData = document.data
                        Log.d("InventoryDAO", "Inventory data: $inventoryData")

                        val inventoryItems = inventoryData?.get("inventoryItems") as? List<Map<String, Any>>
                        if (inventoryItems != null) {
                            Log.d("InventoryDAO", "Processing InventoryItems")

                            inventoryItems.forEach { itemData ->
                                val itemId = itemData["id"] as? String ?: ""
                                val name = itemData["name"] as? String ?: ""
                                val description = itemData["description"] as? String ?: ""
                                val actionMoveData = itemData["actionMove"] as? Map<String, Any>
                                val actionMove = mapActionMove(actionMoveData)
                                val image = itemData["image"] as? String ?: ""
                                val isPicked = itemData["isPicked"] as? Boolean ?: false

                                if (isPicked) {
                                    val item = Item(itemId, name, description, actionMove, image)
                                    pickedItems.add(item)
                                    Log.d("InventoryDAO", "Picked item added: $item")
                                }
                            }

                            onComplete(pickedItems, null)
                            Log.d("InventoryDAO", "Picked items: $pickedItems")
                        } else {
                            Log.d("InventoryDAO", "No inventoryItems found in the document")
                            onComplete(emptyList(), "No inventoryItems in the document for userId: $userId")
                        }
                    }
                } else {
                    Log.d("InventoryDAO", "Inventory not found for userId: $userId")
                    onComplete(emptyList(), "Inventory not found for userId: $userId")
                }
            }
            .addOnFailureListener { e ->
                Log.e("InventoryDAO", "Error fetching inventory for userId: $userId, Error: ${e.message}")
                onComplete(emptyList(), "Error fetching inventory: ${e.message}")
            }
    }

    private fun mapActionMove(actionMoveData: Map<String, Any>?): ActionMove {
        if (actionMoveData != null) {
            val id = actionMoveData["id"] as? String ?: ""
            val name = actionMoveData["name"] as? String ?: ""
            val damage = (actionMoveData["damage"] as? Long)?.toInt() ?: 0
            val image = actionMoveData["image"] as? String ?: ""
            return ActionMove(id, name, damage, image)
        }
        return ActionMove()
    }


    fun checkInventoryExists(userId: String, onComplete: (Boolean) -> Unit) {
        inventoryCollection.document(userId).get()
            .addOnSuccessListener { document ->
                onComplete(document.exists())
            }
            .addOnFailureListener {
                onComplete(false)
            }
    }

    fun updatePickedItems(userId: String, pickedItemIds: List<String>, onComplete: (Boolean, String?) -> Unit) {
        Log.d("InventoryDAO", "Updating picked items for userId: $userId")
        Log.d("InventoryDAO", "Picked Item Ids: $pickedItemIds")
        // First, get the user's email by userId
        userDAO.getUserEmailByUserId(userId) { email, userEmailError ->
            if (userEmailError != null || email == null) {
                Log.e("InventoryDAO", "Error fetching user email: $userEmailError")
                onComplete(false, userEmailError ?: "Error fetching user email")
            } else {
                // Proceed with getting items by userId
                getItemsByUserId(userId) { items, error ->
                    Log.d("InventoryDAO", "Item by Ids: $userId")
                    if (error != null) {
                        onComplete(false, error)
                    } else {
                        val updatedInventoryItems = items.map { item ->
                            mapOf(
                                "id" to item.id,
                                "name" to item.name,
                                "description" to item.description,
                                "actionMove" to mapOf(
                                    "id" to item.actionMove.id,
                                    "name" to item.actionMove.name,
                                    "damage" to item.actionMove.damage,
                                    "image" to item.actionMove.image
                                ),
                                "image" to item.image,
                                "isPicked" to pickedItemIds.contains(item.id)
                            )
                        }

                        // Update the inventory using the retrieved email
                        inventoryCollection.document(email)
                            .update("inventoryItems", updatedInventoryItems)
                            .addOnSuccessListener {
                                Log.i("InventoryDAO", "Inventory updated successfully for userId: $userId")
                                onComplete(true, null)
                            }
                            .addOnFailureListener { e ->
                                Log.e("InventoryDAO", "Error updating inventory for userId: $userId, Error: ${e.message}")
                                onComplete(false, e.message)
                            }
                    }
                }
            }
        }
    }

    fun updateInventoryItems(userId: String, items: List<Item>, onComplete: (Boolean, String?) -> Unit) {
        Log.d("InventoryDAO", "Updating inventory items for userId: $userId")

        // Convert the list of Item objects to a list of maps
        val inventoryItemsList = items.map { item ->
            mapOf(
                "id" to item.id,
                "name" to item.name,
                "description" to item.description,
                "actionMove" to mapOf(
                    "id" to item.actionMove.id,
                    "name" to item.actionMove.name,
                    "damage" to item.actionMove.damage,
                    "image" to item.actionMove.image
                ),
                "image" to item.image,
                "isPicked" to item.isPicked
            )
        }

        // Fetch user email by userId
        userDAO.getUserEmailByUserId(userId) { email, userEmailError ->
            if (userEmailError != null || email == null) {
                Log.e("InventoryDAO", "Error fetching user email: $userEmailError")
                onComplete(false, userEmailError ?: "Error fetching user email")
            } else {
                // Update the inventory in Firestore
                inventoryCollection.document(email)
                    .update("inventoryItems", inventoryItemsList)
                    .addOnSuccessListener {
                        Log.i("InventoryDAO", "Inventory successfully updated for userId: $userId")
                        onComplete(true, null)
                    }
                    .addOnFailureListener { e ->
                        Log.e("InventoryDAO", "Error updating inventory for userId: $userId, Error: ${e.message}")
                        onComplete(false, e.message)
                    }
            }
        }
    }

    fun getPickedItemsByEmail(email: String, onComplete: (List<String>, String?) -> Unit) {
        Log.d("InventoryDAO", "Fetching picked items for email: $email")

        inventoryCollection.document(email).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val inventoryData = document.data
                    Log.d("InventoryDAO", "Fetched inventory data: $inventoryData for email: $email")

                    val pickedItemIds = mutableListOf<String>()
                    val inventoryItems = inventoryData?.get("inventoryItems") as? List<Map<String, Any>>
                    inventoryItems?.forEach { itemData ->
                        val itemId = itemData["id"] as? String ?: ""
                        val isPicked = itemData["isPicked"] as? Boolean ?: false

                        if (isPicked) {
                            pickedItemIds.add(itemId)
                        }
                    }

                    onComplete(pickedItemIds, null)
                } else {
                    Log.d("InventoryDAO", "No inventory found for email: $email")
                    onComplete(emptyList(), "Inventory not found for email: $email")
                }
            }
            .addOnFailureListener { e ->
                Log.e("InventoryDAO", "Error fetching inventory for email: $email, Error: ${e.message}")
                onComplete(emptyList(), "Error fetching inventory: ${e.message}")
            }
    }


    private fun fetchDefaultItems(onAllItemsFetched: (Boolean, String?) -> Unit) {
        val fetchedItemsCount = AtomicInteger(0)
        var errorOccurred = false

        Log.i("InventoryDAO", "Fetching default items: $defaultItemNames")

        defaultItemNames.forEach { itemName ->
            itemDAO.getItemByName(itemName) { item ->
                if (item != null) {
                    defaultItems.add(item)
                    Log.i("InventoryDAO", "Fetched item: ${item.name}")
                } else {
                    errorOccurred = true
                    Log.e("InventoryDAO", "Item not found: $itemName")
                }
                if (fetchedItemsCount.incrementAndGet() == defaultItemNames.size) {
                    if (errorOccurred) {
                        Log.e("InventoryDAO", "Failed to fetch all default items.")
                        onAllItemsFetched(false, "Error fetching default items")
                    } else {
                        Log.i("InventoryDAO", "Successfully fetched all default items.")
                        onAllItemsFetched(true, null)
                    }
                }
            }
        }
    }

    private fun mapItem(itemData: Map<String, Any>): Item {
        val itemId = itemData["id"] as? String ?: ""
        val name = itemData["name"] as? String ?: ""
        val description = itemData["description"] as? String ?: ""
        val actionMoveData = itemData["actionMove"] as? Map<String, Any>
        val actionMove = mapActionMove(actionMoveData)
        val image = itemData["image"] as? String ?: ""
        val isPicked = itemData["isPicked"] as? Boolean ?: false

        return Item(itemId, name, description, actionMove, image, isPicked)
    }

}