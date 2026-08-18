package terraformingmadeeasy.ui.tooltips;

import com.fs.starfarer.api.ui.BaseTooltipCreator;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;

public class OrbitDaysFieldTooltip extends BaseTooltipCreator {
    @Override
    public float getTooltipWidth(Object tooltipParam) {
        return 380f;
    }

    @Override
    public void createTooltip(TooltipMakerAPI tooltip, boolean expanded, Object tooltipParam) {
        tooltip.addPara("Введите число от %s", 0f, Misc.getHighlightColor(), "100 до 10000");
        tooltip.addPara("Число дней, за которое мегаструктура совершает %s вокруг объекта", 10f, Misc.getHighlightColor(), "полный оборот");
    }
}
