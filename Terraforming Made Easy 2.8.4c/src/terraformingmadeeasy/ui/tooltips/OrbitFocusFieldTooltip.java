package terraformingmadeeasy.ui.tooltips;

import com.fs.starfarer.api.ui.BaseTooltipCreator;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;

public class OrbitFocusFieldTooltip extends BaseTooltipCreator {
    @Override
    public float getTooltipWidth(Object tooltipParam) {
        return 380f;
    }

    @Override
    public void createTooltip(TooltipMakerAPI tooltip, boolean expanded, Object tooltipParam) {
        tooltip.addPara("%s, вокруг которого обращается мегаструктура", 0f, Misc.getHighlightColor(), "Основной объект");
        tooltip.addPara("Допустимы только %s", 10f, Misc.getHighlightColor(), "планеты и звёзды");
    }
}
