package com.idlegame

import com.idlegame.dao.ActionMoveDAO
import com.idlegame.dao.EnemyNPCDAO
import com.idlegame.dao.ItemDAO
import com.idlegame.objects.ActionMove
import com.idlegame.objects.EnemyNPC
import com.idlegame.objects.ImageURLs
import com.idlegame.objects.Item

class PopulateDAO {
    private val imageURLs: ImageURLs = ImageURLs()

    fun populateDAO(){
        //
        val actionMove1 = ActionMove("AM1", "Uppercut", 10, imageURLs.actionMoveUppercut)
        val actionMove2 = ActionMove("AM2", "Explosion", 30, imageURLs.actionMoveExplosion)
        val actionMove3 = ActionMove("AM3", "Heal", -25, imageURLs.actionMoveBackpack)
        val actionMove4 = ActionMove("AM4", "Run", 0)

        val item1 = Item("I1", "Cigar", "One will not hurt...", actionMove1, imageURLs.inventoryCigar1)
        val item2 = Item("I2", "Pack of Cigars", "Pack of cigars, good for smoking a lot", actionMove2, imageURLs.inventoryCigar2)
        val item3 = Item("I3", "Shoes", "Gotta keep moving", actionMove3, imageURLs.inventoryShoes1)
        val item4 = Item("I4", "Rainbow Shoes", "Color overload", actionMove4, imageURLs.inventoryShoes2)

        val actionMoveDAO: ActionMoveDAO = ActionMoveDAO()
        val itemDAO: ItemDAO = ItemDAO()

        actionMoveDAO.addActionMove(actionMove1)
        actionMoveDAO.addActionMove(actionMove2)
        actionMoveDAO.addActionMove(actionMove3)
        actionMoveDAO.addActionMove(actionMove4)

        itemDAO.addItem(item1)
        itemDAO.addItem(item2)
        itemDAO.addItem(item3)
        itemDAO.addItem(item4)
        //

        val enemyNPCDAO = EnemyNPCDAO()


        val actionMoveSamurai1 = ActionMove("AMSAMURAI1", "Slash", 30, imageURLs.actionMoveSlash)
        val actionMoveSamurai2 = ActionMove("AMSAMURAI2", "Stab", 25, imageURLs.actionMoveSlash)
        actionMoveDAO.addActionMove(actionMoveSamurai1)
        actionMoveDAO.addActionMove(actionMoveSamurai2)
        val itemSamurai = Item("I9", "Sword", "It's sharp", actionMoveSamurai1, imageURLs.inventorySword)
        itemDAO.addItem(itemSamurai)
        val enemyNpc1 = EnemyNPC(
            "EN1",
            "Samurai The Red Sun",
            imageURLs.battleSamurai,
            imageURLs.battleSamuraiBackground,
            70,
            actionMoveSamurai1,
            actionMoveSamurai2,
            itemSamurai
        )
        enemyNPCDAO.createEnemyNPC(enemyNpc1) { _, _ -> }

        val actionMoveflowerWitch1 = ActionMove("AMFLOWERWITCH1", "Flower Power", 15, imageURLs.actionMoveflowerWitch)
        val actionMoveflowerWitch2 = ActionMove("AMFLOWERWITCH2", "Fear", 35, imageURLs.actionMoveflowerWitch)
        actionMoveDAO.addActionMove(actionMoveflowerWitch1)
        actionMoveDAO.addActionMove(actionMoveflowerWitch2)
        val itemFlowerWitch = Item("I5", "Spikey Jar", "I can feel it trembling...", actionMoveflowerWitch2, imageURLs.inventoryFlowerWitchJar)
        itemDAO.addItem(itemFlowerWitch)
        val enemyNpc2 = EnemyNPC(
            "EN2",
            "Flower Witch",
            imageURLs.battleFlowerWitch,
            imageURLs.battleFlowerWitchBackground,
            100,
            actionMoveflowerWitch1,
            actionMoveflowerWitch2,
            itemFlowerWitch
        )
        enemyNPCDAO.createEnemyNPC(enemyNpc2) { _, _ -> }

        val actionMoveToxic1 = ActionMove("AMTOXIC1", "Explosion", 40, imageURLs.actionMoveToxic)
        val actionMoveToxic2 = ActionMove("AMTOXIC2", "Gas", 10, imageURLs.actionMoveToxic)
        actionMoveDAO.addActionMove(actionMoveToxic1)
        actionMoveDAO.addActionMove(actionMoveToxic2)
        val itemToxic = Item("I6", "Mr. Ugly", "He kinda smells also...", actionMoveToxic1, imageURLs.inventoryToxic)
        itemDAO.addItem(itemToxic)
        val enemyNpc3 = EnemyNPC(
            "EN3",
            "Flower Witch",
            imageURLs.battleToxic,
            imageURLs.battleToxicBackground,
            120,
            actionMoveToxic1,
            actionMoveToxic2,
            itemToxic
        )
        enemyNPCDAO.createEnemyNPC(enemyNpc3) { _, _ -> }

        val actionMoveTreeBeast1 = ActionMove("AMTREEBEAST1", "Roots", 49, imageURLs.actionMoveTreeBeast)
        val actionMoveTreeBeast2 = ActionMove("AMTREEBEAST2", "Cut", 5, imageURLs.actionMoveTreeBeast)
        actionMoveDAO.addActionMove(actionMoveTreeBeast1)
        actionMoveDAO.addActionMove(actionMoveTreeBeast2)
        val itemTreeBeast = Item("I7", "Rub", "A club made out of tree roots.", actionMoveTreeBeast1, imageURLs.inventoryTreeBeast)
        itemDAO.addItem(itemTreeBeast)
        val enemyNpc4 = EnemyNPC(
            "EN4",
            "Tree Beast",
            imageURLs.battleTreeBeast,
            imageURLs.battleTreeBeastBackground,
            120,
            actionMoveTreeBeast1,
            actionMoveTreeBeast2,
            itemTreeBeast
        )
        enemyNPCDAO.createEnemyNPC(enemyNpc4) { _, _ -> }

        val actionMoveUndead1 = ActionMove("AMUNDEAD1", "FISH ATTACK", 30, imageURLs.actionMoveUndead)
        val actionMoveUndead2 = ActionMove("AMUNDEAD2", "Slam", 20, imageURLs.actionMoveUndead)
        actionMoveDAO.addActionMove(actionMoveUndead1)
        actionMoveDAO.addActionMove(actionMoveUndead2)
        val itemUndead = Item("I8", "Splashy", "A potion full of mystery and color", actionMoveUndead1, imageURLs.inventoryUndead)
        itemDAO.addItem(itemUndead)
        val enemyNpc5 = EnemyNPC(
            "EN5",
            "Tree Beast",
            imageURLs.battleTreeBeast,
            imageURLs.battleTreeBeastBackground,
            80,
            actionMoveUndead1,
            actionMoveUndead2,
            itemUndead
        )
        enemyNPCDAO.createEnemyNPC(enemyNpc5) { _, _ -> }
    }
}