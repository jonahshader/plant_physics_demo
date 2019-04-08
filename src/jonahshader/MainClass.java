package jonahshader;

import processing.core.PApplet;

import java.util.ArrayList;

public class MainClass extends PApplet {
    private ArrayList<TreeBranch> trees;
    public static final float PIXELS_PER_METER = 16;

    @Override
    public void settings() {
        size(1920, 1080, P2D);
//        smooth(16);
        noSmooth();
    }

    @Override
    public void setup() {
        System.out.println("Creating tree");
//        tree = new TreeBranch(new double[]{width / 2.0, height}, 90, -Math.PI / 2.0, 7);
        trees = new ArrayList<>();
        System.out.println("Finished creating tree");
//        frameRate(500);
        background(110, 130, 255);
    }

    @Override
    public void draw() {
        background(110, 130, 255);
        scale(PIXELS_PER_METER, -PIXELS_PER_METER); // every 64 pixels is one meter
        translate(0, -height / PIXELS_PER_METER);
//        tree.run();
//        tree.draw(this);
//        trees.forEach(TreeBranch::run);
        for (TreeBranch tree : trees) {
            tree.run(frameCount / 60.0);
        }
        trees.forEach(treeBranch -> treeBranch.draw(this));
        System.out.println(frameRate + " " + trees.size());
    }

    @Override
    public void mousePressed() {
        if (mouseButton == LEFT) {
            double scale = Math.random() + 1;
            trees.add(new TreeBranch(new double[]{mouseX / PIXELS_PER_METER, 0}, 8 * scale, 0.6 * scale, Math.PI / 2.0, 4));
        } else {
            if (trees.size() > 1) {
                TreeBranch closestTreeBranch = trees.get(0);
                double closestDist = dist(mouseX / PIXELS_PER_METER, 0, (float) closestTreeBranch.getX(), 0);
                for (TreeBranch treeBranch : trees) {
                    double dist = dist(mouseX / PIXELS_PER_METER, 0, (float) treeBranch.getX(), 0);
                    if (dist < closestDist) {
                        closestDist = dist;
                        closestTreeBranch = treeBranch;
                    }
                }
                trees.remove(closestTreeBranch);
            } else if (trees.size() == 1) {
                trees.remove(0);
            }
        }
    }

    public static void main(String[] args) {
        PApplet.main("jonahshader.MainClass");
    }
}
