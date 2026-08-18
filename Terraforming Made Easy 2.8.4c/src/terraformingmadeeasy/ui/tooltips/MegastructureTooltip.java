package terraformingmadeeasy.ui.tooltips;

import com.fs.starfarer.api.impl.campaign.ids.Entities;
import com.fs.starfarer.api.ui.BaseTooltipCreator;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import terraformingmadeeasy.Utils;
import terraformingmadeeasy.ids.TMEIds;

import java.util.Objects;

public class MegastructureTooltip extends BaseTooltipCreator {
    public Utils.ProjectData project;

    public MegastructureTooltip(Utils.ProjectData project) {
        this.project = project;
    }

    @Override
    public float getTooltipWidth(Object tooltipParam) {
        return 380f;
    }

    @Override
    public void createTooltip(TooltipMakerAPI tooltip, boolean expanded, Object tooltipParam) {
        if (Objects.equals(this.project.id, Entities.CORONAL_TAP)) {
            tooltip.addPara("В системе может существовать только %s", 0f, Misc.getHighlightColor(), "1: " + this.project.name);
            tooltip.addPara("В системе требуется %s", 10f, Misc.getHighlightColor(), "голубой сверхгигант");
        } else if (Objects.equals(this.project.id, Entities.DERELICT_CRYOSLEEPER)) {
            tooltip.addPara("В системе может существовать только %s", 0f, Misc.getHighlightColor(), "1: " + this.project.name);
        } else if (Objects.equals(this.project.id, Entities.INACTIVE_GATE)) {
            tooltip.addPara("В системе может существовать только %s", 0f, Misc.getHighlightColor(), "1: " + this.project.name);
        } else if (Objects.equals(this.project.id, TMEIds.TME_STATION)) {
            tooltip.addPara("В системе может существовать не более %s", 0f, Misc.getHighlightColor(), "3: " + this.project.name);
        } else {
            tooltip.addPara("В системе может существовать только %s", 0f, Misc.getHighlightColor(), "1: " + this.project.name);
        }
    }
}
