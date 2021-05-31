package com.alessiodp.securityvillagers.bukkit.nms.v1_8_R3;

import com.alessiodp.securityvillagers.bukkit.utils.IDataNMS;
import com.alessiodp.securityvillagers.bukkit.villagers.objects.MobsType;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class DataNMS implements IDataNMS {
	
	@Override
	public void populateMobs() {
		MobsType.WANDERING_TRADER.setMobClass(null);
		MobsType.TRADER_LLAMA.setMobClass(null);
		
		MobsType.EVOKER.setMobClass(null);
		MobsType.EVOKER_FANGS.setMobClass(null);
		MobsType.ILLUSIONER.setMobClass(null);
		MobsType.PILLAGER.setMobClass(null);
		MobsType.RAVAGER.setMobClass(null);
		MobsType.STRAY.setMobClass(null);
		MobsType.VEX.setMobClass(null);
		MobsType.VINDICATOR.setMobClass(null);
	}
	
	@Override
	public Material getHandMaterial(Player player) {
		return player.getItemInHand().getType();
	}
	
	@Override
	public boolean existOffHand() {
		return false;
	}
	
	@Override
	public Material getVillagerEgg() {
		return Material.MONSTER_EGG;
	}
	
	@Override
	public boolean isHarvestable(Material material) {
		return material == Material.WHEAT
				|| material == Material.POTATO
				|| material == Material.CARROT;
	}
}
