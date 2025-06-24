package org.xiaoxingbomei.common.entity;

import lombok.Getter;

/**
 * 进度条
 */

public class ProgressBar
{

    private final int total;
    private final int width;
    private int current;
    private Style style;

    private final static Style STYLE_1 = new Style("#", " ", "");
    private final static Style STYLE_2 = new Style("#", "=", "");
    private final static Style STYLE_3 = new Style("=", " ", ">");

    public ProgressBar(int total, int width, int style)
    {
        this.total = total;
        this.width = width;
        this.current = 0;
        if (style == 1) {
            this.style = STYLE_1;
        } else if (style == 2) {
            this.style = STYLE_2;
        } else {
            this.style = STYLE_3;
        }
    }

    public void start()
    {
        System.out.println("\n开始执行任务...");
    }

    public void finish()
    {
        System.out.println("\n任务执行结束...");
    }

    public void update(int progress)
    {
        this.current = progress;
        print();
    }

    private void print()
    {
        double percentage = (double) current / total;
        int progressMarks = (int) (percentage * width);

        StringBuilder bar = new StringBuilder();
        bar.append(String.format("当前进度: %3d%% [", (int) (percentage * 100)));
        boolean firstRight = true;
        for (int i = 0; i < width; i++)
        {
            if (i < progressMarks) {
                bar.append(style.leftStr);
            } else {
                if (firstRight) {
                    bar.append(style.leftEndStr);
                    firstRight = false;
                }
                bar.append(style.rightStr);
            }
        }
        bar.append("]");

        System.out.print("\r" + bar);
        if (percentage >= 1) {
            finish();
        }
    }


    @Getter
    private static class Style
    {
        private String leftStr;
        private String rightStr;
        private String leftEndStr;

        public Style(String leftStr, String rightStr, String leftEndStr) {
            this.leftStr = leftStr;
            this.rightStr = rightStr;
            this.leftEndStr = leftEndStr;
        }
    }

}
