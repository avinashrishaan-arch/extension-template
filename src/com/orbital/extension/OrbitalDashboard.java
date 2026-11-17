
package com.orbital.extension;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.view.MotionEvent;
import android.view.View;

import com.google.appinventor.components.annotations.DesignerComponent;
import com.google.appinventor.components.annotations.SimpleEvent;
import com.google.appinventor.components.annotations.SimpleFunction;
import com.google.appinventor.components.annotations.SimpleObject;
import com.google.appinventor.components.common.ComponentCategory;
import com.google.appinventor.components.runtime.AndroidViewComponent;
import com.google.appinventor.components.runtime.ComponentContainer;
import com.google.appinventor.components.runtime.EventDispatcher;

import java.util.ArrayList;

@DesignerComponent(
    version = 1,
    description = "ORBITAL space-style skill and career dashboard",
    category = ComponentCategory.USERINTERFACE,
    nonVisible = false,
    iconName = "https://www.gstatic.com/images/branding/product/1x/appinventor_48dp.png"
)
@SimpleObject(external = true)
public class OrbitalDashboard extends AndroidViewComponent {

    private OrbitalView orbitalView;
    private ArrayList<String> skillNames = new ArrayList<String>();
    private ArrayList<String> skillCategories = new ArrayList<String>();
    private ArrayList<Integer> skillMastery = new ArrayList<Integer>();
    private ArrayList<String> careerNames = new ArrayList<String>();
    private ArrayList<Integer> careerReadiness = new ArrayList<Integer>();
    private String currentTheme = "Dark";

    public OrbitalDashboard(ComponentContainer container) {
        super(container);
        orbitalView = new OrbitalView(container.$context());
        container.$add(this);
    }

    @Override
    public View getView() {
        return orbitalView;
    }

    @SimpleFunction(description = "Add a skill planet. Mastery should be between 0 and 100.")
    public void AddSkill(String name, String category, int mastery) {
        skillNames.add(name);
        skillCategories.add(category);
        skillMastery.add(Math.max(0, Math.min(100, mastery)));
        Refresh();
    }

    @SimpleFunction(description = "Add a career satellite. Readiness should be between 0 and 100.")
    public void AddCareer(String name, int readiness) {
        careerNames.add(name);
        careerReadiness.add(Math.max(0, Math.min(100, readiness)));
        Refresh();
    }

    @SimpleFunction(description = "Remove all skills and careers.")
    public void ClearOrbit() {
        skillNames.clear();
        skillCategories.clear();
        skillMastery.clear();
        careerNames.clear();
        careerReadiness.clear();
        Refresh();
    }

    @SimpleFunction(description = "Remove all skill planets.")
    public void ClearSkills() {
        skillNames.clear();
        skillCategories.clear();
        skillMastery.clear();
        Refresh();
    }

    @SimpleFunction(description = "Remove all career satellites.")
    public void ClearCareers() {
        careerNames.clear();
        careerReadiness.clear();
        Refresh();
    }

    @SimpleFunction(description = "Change the dashboard theme to Dark, Grey, or Light.")
    public void SetTheme(String theme) {
        if (theme != null) {
            String t = theme.toLowerCase();
            if (t.equals("dark")) currentTheme = "Dark";
            else if (t.equals("grey") || t.equals("gray")) currentTheme = "Grey";
            else if (t.equals("light")) currentTheme = "Light";
        }
        Refresh();
    }

    @SimpleFunction(description = "Redraw the dashboard.")
    public void Refresh() {
        if (orbitalView != null) orbitalView.invalidate();
    }

    @SimpleFunction(description = "Get the number of skill planets.")
    public int SkillCount() {
        return skillNames.size();
    }

    @SimpleFunction(description = "Get the number of career satellites.")
    public int CareerCount() {
        return careerNames.size();
    }

    @SimpleEvent(description = "Triggered when a skill planet is tapped.")
    public void SkillSelected(String name) {
        EventDispatcher.dispatchEvent(this, "SkillSelected", name);
    }

    @SimpleEvent(description = "Triggered when a career satellite is tapped.")
    public void CareerSelected(String name) {
        EventDispatcher.dispatchEvent(this, "CareerSelected", name);
    }

    @SimpleEvent(description = "Triggered when a navigation tab is tapped.")
    public void NavigationSelected(String tab) {
        EventDispatcher.dispatchEvent(this, "NavigationSelected", tab);
    }

    private class OrbitalView extends View {

        private Paint paint = new Paint(3);
        private float centerX;
        private float centerY;
        private float scale;

        OrbitalView(Context context) {
            super(context);
            setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }

        private int backgroundColor() {
            if (currentTheme.equals("Light")) return Color.rgb(241, 245, 250);
            if (currentTheme.equals("Grey")) return Color.rgb(45, 49, 58);
            return Color.rgb(10, 13, 27);
        }

        private int textColor() {
            return currentTheme.equals("Light") ?
                Color.rgb(30, 38, 55) : Color.WHITE;
        }

        private void circle(Canvas c, float x, float y, float r, int color) {
            paint.setColor(color);
            paint.setStyle(Paint.Style.FILL);
            c.drawCircle(x, y, r, paint);
        }

        private void text(Canvas c, String value, float x, float y,
                          float size, int color) {
            paint.setColor(color);
            paint.setTextSize(size);
            paint.setTypeface(Typeface.create("sans-serif", Typeface.NORMAL));
            paint.setTextAlign(Paint.Align.CENTER);
            paint.setStyle(Paint.Style.FILL);
            c.drawText(value, x, y, paint);
        }

        private void ring(Canvas c, float radius) {
            paint.setColor(currentTheme.equals("Light") ?
                Color.rgb(190, 200, 215) : Color.rgb(60, 75, 105));
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(1.5f);
            c.drawCircle(centerX, centerY, radius, paint);
            paint.setStyle(Paint.Style.FILL);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);

            float w = getWidth();
            float h = getHeight();
            if (w <= 0 || h <= 0) return;

            centerX = w / 2f;
            centerY = h * 0.46f;
            scale = Math.min(w, h) / 360f;

            canvas.drawColor(backgroundColor());

            text(canvas, "O R B I T A L", centerX, 28 * scale,
                16 * scale, textColor());
            text(canvas, "YOUR SKILLS • YOUR FUTURE",
                centerX, 45 * scale, 8 * scale,
                currentTheme.equals("Light") ?
                Color.DKGRAY : Color.LTGRAY);

            float orbit = 105 * scale;
            ring(canvas, orbit);
            ring(canvas, orbit * 0.68f);

            // Draw the central user star.
            circle(canvas, centerX, centerY, 27 * scale,
                Color.rgb(255, 190, 75));
            circle(canvas, centerX, centerY, 19 * scale,
                Color.rgb(255, 225, 145));
            text(canvas, "YOU", centerX, centerY + 4 * scale,
                10 * scale, Color.rgb(65, 45, 20));

            // Draw skill planets.
            for (int i = 0; i < skillNames.size(); i++) {
                double angle = (Math.PI * 2 * i /
                    Math.max(1, skillNames.size())) - Math.PI / 2;

                float x = centerX + (float)Math.cos(angle) *
                    orbit * 0.68f;
                float y = centerY + (float)Math.sin(angle) *
                    orbit * 0.68f;

                int mastery = skillMastery.get(i);
                float radius = (7 + mastery * 0.10f) * scale;

                String category = skillCategories.get(i).toLowerCase();
                int color = Color.rgb(85, 170, 255);

                if (category.contains("creative"))
                    color = Color.rgb(190, 115, 255);
                else if (category.contains("science"))
                    color = Color.rgb(70, 220, 190);
                else if (category.contains("business"))
                    color = Color.rgb(255, 180, 75);

                circle(canvas, x, y, radius, color);
                text(canvas, skillNames.get(i), x,
                    y + radius + 12 * scale, 8 * scale, textColor());
            }

            // Draw career satellites.
            for (int i = 0; i < careerNames.size(); i++) {
                float x = centerX + (i % 2 == 0 ? -1 : 1) *
                    145 * scale;
                float y = centerY + (i / 2) * 35 * scale;

                circle(canvas, x, y, 9 * scale,
                    Color.rgb(255, 190, 90));
                text(canvas, careerNames.get(i), x,
                    y + 22 * scale, 8 * scale, textColor());
            }

            // Bottom navigation.
            float navY = h - 22 * scale;
            text(canvas, "SKILLS", w * 0.20f, navY,
                10 * scale, textColor());
            text(canvas, "CAREERS", w * 0.50f, navY,
                10 * scale, textColor());
            text(canvas, "DEVLOG", w * 0.80f, navY,
                10 * scale, textColor());

            text(canvas, skillNames.size() + " SKILLS",
                centerX, h - 43 * scale, 8 * scale,
                currentTheme.equals("Light") ?
                Color.DKGRAY : Color.LTGRAY);
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            if (event.getAction() != MotionEvent.ACTION_UP) return true;

            float x = event.getX();
            float y = event.getY();
            float w = getWidth();
            float h = getHeight();

            if (y > h - 45 * scale) {
                if (x < w * 0.34f) NavigationSelected("Skills");
                else if (x < w * 0.67f) NavigationSelected("Careers");
                else NavigationSelected("Devlog");
                return true;
            }

            float orbit = 105 * scale * 0.68f;

            for (int i = 0; i < skillNames.size(); i++) {
                double angle = (Math.PI * 2 * i /
                    Math.max(1, skillNames.size())) - Math.PI / 2;

                float sx = centerX + (float)Math.cos(angle) * orbit;
                float sy = centerY + (float)Math.sin(angle) * orbit;

                float dx = x - sx;
                float dy = y - sy;

                if (dx * dx + dy * dy < 30 * 30 * scale * scale) {
                    SkillSelected(skillNames.get(i));
                    return true;
                }
            }

            return true;
        }
    }
}
