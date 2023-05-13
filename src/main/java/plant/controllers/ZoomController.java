package plant.controllers;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

public class ZoomController extends StackPane {

    private int deltaCount = 0;
    private final double DEFAULT_ZOOM = 1.0;
    private DoubleProperty zoomMax = new SimpleDoubleProperty(10.0);
    private DoubleProperty zoomMin = new SimpleDoubleProperty(0.1);
    private DoubleProperty zoomDelta = new SimpleDoubleProperty(1.2);
    private DoubleProperty zoom = new SimpleDoubleProperty(DEFAULT_ZOOM);
    private Group contentWrapper;

    public ZoomController(final Node content) {
        super();
        zoom.addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> ov, Number t, Number t1) {
                System.out.println("Zoom=" + zoom.doubleValue());
                content.scaleXProperty().bind(zoom);
                content.scaleYProperty().bind(zoom);
                content.translateXProperty();
            }
        });

        content.setOnScroll(new EventHandler<ScrollEvent>() {
            public void handle(ScrollEvent event) {
                if (event.getDeltaY() > 0) {
                    zoomIn();
                } else {
                    zoomOut();
                }
            }
        });
    }

    public void zoomIn() {
        double zoomValue = DEFAULT_ZOOM * Math.pow(zoomDelta.get(), deltaCount + 1);
        System.out.println("Zoooom " + zoomValue);
        if (zoomValue > zoomMax.get()) {
            setZoom(zoomMax.get());
            return;
        }

        deltaCount++;
        setZoom(zoomValue);

    }

    public void zoomOut() {
        double zoomValue = DEFAULT_ZOOM * Math.pow(zoomDelta.get(), deltaCount - 1);
        System.out.println("Zoooom " + zoomValue);
        if (zoomValue < zoomMin.get()) {
            setZoom(zoomMin.get());
            return;
        }

        deltaCount--;
        setZoom(zoomValue);
    }

    public void setZoom(double zoomValue) {
        zoom.set(zoomValue);
    }
}