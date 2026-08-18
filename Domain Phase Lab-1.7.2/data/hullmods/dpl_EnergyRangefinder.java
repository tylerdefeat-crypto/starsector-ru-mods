package data.hullmods;

import java.awt.Color;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.combat.WeaponAPI;
import com.fs.starfarer.api.combat.WeaponAPI.AIHints;
import com.fs.starfarer.api.combat.WeaponAPI.WeaponSize;
import com.fs.starfarer.api.combat.WeaponAPI.WeaponType;
import com.fs.starfarer.api.combat.listeners.WeaponBaseRangeModifier;
import com.fs.starfarer.api.loading.WeaponSlotAPI;
import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;

public class dpl_EnergyRangefinder extends BaseHullMod {

	public static float BONUS_MAX_1 = 700;
	public static float BONUS_MAX_2 = 750;
	public static float BONUS_MAX_3 = 800;
	public static float BONUS_SMALL_1 = 100;
	public static float BONUS_SMALL_2 = 100;
	public static float BONUS_SMALL_3 = 150;
	public static float BONUS_MEDIUM_3 = 100;
	
	public static float HYBRID_MULT = 0.5f;
	public static float HYBRID_BONUS_MIN = 50f;
	
	
	public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
	}

	public static WeaponSize getLargestEnergySlot(ShipAPI ship) {
		if (ship == null) return null;
		WeaponSize largest = null;
		for (WeaponSlotAPI slot : ship.getHullSpec().getAllWeaponSlotsCopy()) {
			if (slot.isDecorative() ) continue;
			if (slot.getWeaponType() == WeaponType.ENERGY) {
				if (largest == null || largest.ordinal() < slot.getSlotSize().ordinal()) {
					largest = slot.getSlotSize();
				}
			}
		}
		return largest;
	}
	
	@Override
	public void applyEffectsAfterShipCreation(ShipAPI ship, String id) {
		WeaponSize largest = getLargestEnergySlot(ship);
		if (largest == null) return;
		float small = 0f;
		float medium = 0f;
		float max = 0f;
		if (largest.getDisplayName() == "Large") {
			small = BONUS_SMALL_3;
			medium = BONUS_MEDIUM_3;
			max = BONUS_MAX_3;
		} else if (largest.getDisplayName() == "Medium") {
			small = BONUS_SMALL_2;
			max = BONUS_MAX_2;
		} else if (largest.getDisplayName() == "Small") {
			small = BONUS_SMALL_1;
			max = BONUS_MAX_1;
		}
		
		ship.addListener(new RangefinderRangeModifier(small, medium, max));
	}
	
	public static class RangefinderRangeModifier implements WeaponBaseRangeModifier {
		public float small, medium, max;
		public RangefinderRangeModifier(float small, float medium, float max) {
			this.small = small;
			this.medium = medium;
			this.max = max;
		}
		
		public float getWeaponBaseRangePercentMod(ShipAPI ship, WeaponAPI weapon) {
			return 0;
		}
		public float getWeaponBaseRangeMultMod(ShipAPI ship, WeaponAPI weapon) {
			return 1f;
		}
		public float getWeaponBaseRangeFlatMod(ShipAPI ship, WeaponAPI weapon) {
			if (weapon.getSpec() == null) {
				return 0f;
			}
			if (weapon.getSpec().getMountType() != WeaponType.ENERGY && 
					weapon.getSpec().getMountType() != WeaponType.HYBRID) {
				return 0f;
			}
			if (weapon.hasAIHint(AIHints.PD)) {
				return 0f;
			}
			
			float bonus = 0;
			if (weapon.getSize() == WeaponSize.SMALL) {
				bonus = small;
			} else if (weapon.getSize() == WeaponSize.MEDIUM) {
				bonus = medium;
			}
			if (weapon.getSpec().getMountType() == WeaponType.HYBRID) {
				bonus *= HYBRID_MULT;
				if (bonus < HYBRID_BONUS_MIN) {
					bonus = HYBRID_BONUS_MIN;
				}
			}
			if (bonus == 0f) return 0f;
			
			float base = weapon.getSpec().getMaxRange();
			if (base + bonus > max) {
				bonus = max - base;
			}
			if (bonus < 0) bonus = 0;
			return bonus;
		}
	}

	public String getDescriptionParam(int index, HullSize hullSize) {
		//if (index == 0) return "" + (int)RANGE_PENALTY_PERCENT + "%";
		return null;
	}
	
	@Override
	public boolean shouldAddDescriptionToTooltip(HullSize hullSize, ShipAPI ship, boolean isForModSpec) {
		return false;
	}

	@Override
	public void addPostDescriptionSection(TooltipMakerAPI tooltip, HullSize hullSize, ShipAPI ship, float width, boolean isForModSpec) {
		float pad = 3f;
		float opad = 10f;
		Color h = Misc.getHighlightColor();
		Color bad = Misc.getNegativeHighlightColor();
		Color t = Misc.getTextColor();
		Color g = Misc.getGrayColor();
		
		WeaponSize largest = getLargestEnergySlot(ship);
		
		
		
		tooltip.addPara("Использует данные наведения крупнейшего энергетического слота корабля, "
				+ "увеличивая базовую дальность некоторых энергетических орудий до уровня аналогичного оружия большего размера. "
				+ "Также действует на гибридное оружие. На оружие ПВО не влияет.",
				opad, h, "крупнейшего энергетического слота", "базовую дальность");
		
		tooltip.addPara("Бонус дальности зависит от размера крупнейшего энергетического слота. "
				+ "Увеличенная базовая дальность ограничена, но на неё продолжают действовать другие модификаторы.", opad);
		
		tooltip.addSectionHeading("Дальность энергетического оружия", Alignment.MID, opad);
		
		tooltip.addPara("Действует на малое и среднее энергетическое оружие.", opad);
		
		float col1W = 120;
		float colW = (int) ((width - col1W - 12f) / 3f);
		float lastW = colW;
		
		tooltip.beginTable(Misc.getBasePlayerColor(), Misc.getDarkPlayerColor(), Misc.getBrightPlayerColor(),
				20f, true, true, 
				new Object [] {"Макс. энерг. слот", col1W, "Малое", colW, "Среднее", colW, "Предел", lastW});
		
		
		Color c = null;
		if (largest == WeaponSize.SMALL) c = h;
		else if (largest == WeaponSize.MEDIUM) c = h;
		else c = g;
		tooltip.addRow(Alignment.MID, c, "Малый / Средний",
				Alignment.MID, c, "+" + (int) BONUS_SMALL_1,
				Alignment.MID, g, "---",
				Alignment.MID, c, "" + (int)BONUS_MAX_1);
		
		if (largest == WeaponSize.LARGE) c = h;
		else c = g;
		tooltip.addRow(Alignment.MID, c, "Большой",
				Alignment.MID, c, "+" + (int) BONUS_SMALL_3,
				Alignment.MID, c, "+" + (int) BONUS_MEDIUM_3,
				Alignment.MID, c, "" + (int)BONUS_MAX_3);
		
		tooltip.addTable("", 0, opad);

		
		tooltip.addSectionHeading("Дальность гибридного оружия", Alignment.MID, opad + 7f);
		
		tooltip.addPara("Действует на гибридное оружие всех размеров — его можно устанавливать как в баллистические, так и в энергетические слоты.", opad);
		
		col1W = 120;
		colW = (int) ((width - col1W - lastW - 15f) / 3f);
		
		tooltip.beginTable(Misc.getBasePlayerColor(), Misc.getDarkPlayerColor(), Misc.getBrightPlayerColor(),
				20f, true, true, 
				new Object [] {"Макс. энерг. слот", col1W, "Малое", colW, "Среднее", colW, "Большое", colW, "Предел", lastW});
		
		
		c = null;
		if (largest == WeaponSize.SMALL) c = h;
		else if (largest == WeaponSize.MEDIUM) c = h;
		else c = g;
		tooltip.addRow(Alignment.MID, c, "Малый / Средний",
				Alignment.MID, c, "+" + (int) (BONUS_SMALL_1 * HYBRID_MULT),
				Alignment.MID, c, "+" + (int) HYBRID_BONUS_MIN,
				Alignment.MID, c, "+" + (int) HYBRID_BONUS_MIN,
				Alignment.MID, c, "" + (int)BONUS_MAX_1);
		
		if (largest == WeaponSize.LARGE) c = h;
		else c = g;
		tooltip.addRow(Alignment.MID, c, "Большой",
				Alignment.MID, c, "+" + (int) (BONUS_SMALL_3 * HYBRID_MULT),
				Alignment.MID, c, "+" + (int) (BONUS_MEDIUM_3 * HYBRID_MULT),
				Alignment.MID, c, "+" + (int) HYBRID_BONUS_MIN,
				Alignment.MID, c, "" + (int)BONUS_MAX_3);
		
		tooltip.addTable("", 0, opad);
		
		
		tooltip.addSectionHeading("Взаимодействие с другими модификаторами", Alignment.MID, opad + 7f);
		tooltip.addPara("Поскольку увеличивается базовая дальность, этот бонус, в отличие от большинства других фиксированных модификаторов, "
				+ "усиливается процентными бонусами других модификаций корпуса и навыков.", opad);
	}
	
	public float getTooltipWidth() {
		return 412f;
	}
	
	@Override
	public boolean isApplicableToShip(ShipAPI ship) {
		WeaponSize largest = getLargestEnergySlot(ship);
		if (ship != null && largest == null) {
			return false;
		}
		return getUnapplicableReason(ship) == null;
	}
	
	public String getUnapplicableReason(ShipAPI ship) {
		WeaponSize largest = getLargestEnergySlot(ship);
		if (ship != null && largest == null) {
			return "На корабле нет энергетических орудийных слотов";
		}
		if (ship != null && 
				ship.getHullSize() != HullSize.CAPITAL_SHIP && 
				ship.getHullSize() != HullSize.DESTROYER && 
				ship.getHullSize() != HullSize.CRUISER) {
			return "Можно установить только на эсминцы и более крупные корабли";
		}
		return null;
	}
	
}








