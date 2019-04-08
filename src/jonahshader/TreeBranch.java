package jonahshader;

import processing.core.PApplet;
import processing.core.PConstants;

import java.util.ArrayList;

public class TreeBranch {

    // physics constants
    private static final double JOINT_NEWTON_METERS_PER_RAD = -70.0;  // rotational spring constant
    private static final double WOOD_MASS_PER_METER_CUBED = 550.0;
    private static final double GRAVITY = -9.80665;

    private ArrayList<TreeBranch> branches;
    private TreeBranch parentBranch;

    private int children, iteration;
    private double length, targetAngle, realAngle, radius, angleOffset;
    private double angularVelocity = 0;
    private double[] pos, endPos;

    // constructor for child branches
    private TreeBranch(TreeBranch parentBranch) {
        this.parentBranch = parentBranch;
//        this.children = parentBranch.children + (Math.random() > .5 ? -1 : 1);
        this.children = parentBranch.children;
//        this.iteration = parentBranch.iteration - Math.random() > 0.75 ? 1 : 0;]
        this.iteration = parentBranch.iteration - 1;
        pos = parentBranch.endPos; // don't clone, reference the same one so that this branch will move with the parentBranch
        double scaleDown = 0.9 - (Math.random() * 0.3);
//        double scaleDown = 1.5;
        length = parentBranch.length * scaleDown;
        radius = parentBranch.radius * scaleDown * 0.9;
        angleOffset = (Math.random() - 0.5) * Math.PI * 0.5;
//        angleOffset = 0;
//        angleOffset = 0.3;
        calculateTargetAngle();
        realAngle = targetAngle;

        endPos = new double[2];
        branches = new ArrayList<>();
        createOtherBranches();
        run(0);
    }

    public TreeBranch(double[] pos, double length, double radius, double targetAngle, int iterations) {
        this.pos = pos.clone();
        this.length = length;
        this.radius = radius;
        this.targetAngle = targetAngle;
        children = 3;
        iteration = iterations;
        realAngle = targetAngle;
        angleOffset = 0;

        parentBranch = null; // this is the first branch
        endPos = new double[2];
        calculateEndPos();

        branches = new ArrayList<>();
        createOtherBranches();

        run(0);
    }

    private void createOtherBranches() {
//        if (children == 1) {
//            for (int i = 0; i < 2; i++) {
//                branches.add(new TreeBranch(this));
//            }
//        } else {
//            for (int i = 0; i < children; i++) {
//                branches.add(new TreeBranch(this));
//            }
//        }
        if (iteration > 0) {
            for (int i = 0; i < children; i++) {
                branches.add(new TreeBranch(this));
            }
        }

    }

    public void run(double time) {
        // do gravity stuff
        calculateTargetAngle();
        double gravAngAccel = (1.5 * Math.cos(realAngle) * GRAVITY) / length;
        double windAngAccel = (1.5 * Math.sin(realAngle) * Math.sin(time * 1.0)) / length;
        windAngAccel *= 1;
        windAngAccel /= radius;
        windAngAccel *= length;

        double springTorque = (realAngle - targetAngle) * JOINT_NEWTON_METERS_PER_RAD;
        angularVelocity += gravAngAccel / 60;
        angularVelocity += springTorque / 60;
        angularVelocity += windAngAccel / 60;
        angularVelocity *= .6;
        realAngle += angularVelocity;
        calculateEndPos();
        // update children
        for (TreeBranch child : branches) {
            child.run(time);
        }
    }

    private void calculateEndPos() {
        endPos[0] = pos[0] + Math.cos(realAngle) * length;
        endPos[1] = pos[1] + Math.sin(realAngle) * length;
    }

    private void calculateTargetAngle() {
        if (parentBranch != null) {
            targetAngle = parentBranch.realAngle + angleOffset;
        }
    }

    public void draw(PApplet graphics) {
        if (iteration == 0) {
            graphics.stroke(0, 255, 0);
            graphics.fill(0, 255, 0);
            graphics.ellipseMode(PConstants.CENTER);
            graphics.ellipse((float) endPos[0], (float) endPos[1], (float) radius * 3, (float) radius * 3);
        } else {
            int downScale = iteration * 8;
            graphics.stroke(150 - downScale, 80 - downScale, 90 - downScale);
//            graphics.stroke(0, 255, 0);
        }

        graphics.strokeWeight((float) (radius * 2));

        graphics.line((float) pos[0], (float) pos[1], (float) endPos[0], (float) endPos[1]);
        for (TreeBranch child : branches) {
            child.draw(graphics);
        }
    }

    public double getX() {
        return pos[0];
    }
}
