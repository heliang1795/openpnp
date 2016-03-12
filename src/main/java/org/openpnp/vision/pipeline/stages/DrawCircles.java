package org.openpnp.vision.pipeline.stages;

import java.awt.Color;
import java.util.List;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.openpnp.util.HslColor;
import org.openpnp.vision.FluentCv;
import org.openpnp.vision.pipeline.CvPipeline;
import org.openpnp.vision.pipeline.CvStage;
import org.openpnp.vision.pipeline.CvStage.Result.Circle;
import org.openpnp.vision.pipeline.stages.convert.ColorConverter;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.convert.Convert;

public class DrawCircles extends CvStage {
    @Element(required = false)
    @Convert(ColorConverter.class)
    private Color color = null;

    @Attribute(required = false)
    private String modelStageName = null;

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public String getModelStageName() {
        return modelStageName;
    }

    public void setModelStageName(String modelStageName) {
        this.modelStageName = modelStageName;
    }

    @Override
    public Result process(CvPipeline pipeline) throws Exception {
        if (modelStageName == null) {
            return null;
        }
        Result result = pipeline.getResult(modelStageName);
        if (result == null || result.model == null) {
            return null;
        }
        Mat mat = pipeline.getWorkingImage();
        List<Result.Circle> circles = (List<Result.Circle>) result.model;
        for (int i = 0; i < circles.size(); i++) {
            Result.Circle circle = circles.get(i);
            Color color = this.color == null ? FluentCv.indexedColor(i) : this.color;
            Color centerColor = new HslColor(color).getComplementary();
            Core.circle(mat, new Point(circle.x, circle.y), (int) (circle.diameter / 2),
                    FluentCv.colorToScalar(color), 2);
            Core.circle(mat, new Point(circle.x, circle.y), 1, FluentCv.colorToScalar(centerColor),
                    2);
        }
        return null;
    }
}
