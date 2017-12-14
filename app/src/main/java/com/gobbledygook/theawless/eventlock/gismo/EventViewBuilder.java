package com.gobbledygook.theawless.eventlock.gismo;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.graphics.drawable.shapes.RectShape;
import android.support.v7.widget.GridLayoutManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gobbledygook.theawless.eventlock.helper.Enums;

class EventViewBuilder {
    private final Context gismoContext;
    RelativeLayout fullContainerRelativeLayout;
    private LinearLayout textContainerLinearLayout;
    private ImageView colorImageView;

    EventViewBuilder(Context gismoContext) {
        this.gismoContext = gismoContext;
    }

    void setupFullContainerRelativeLayout(int[] eventViewDimensions) {
        fullContainerRelativeLayout = new RelativeLayout(gismoContext);
        fullContainerRelativeLayout.setLayoutParams(new GridLayoutManager.LayoutParams(eventViewDimensions[0], eventViewDimensions[1]));
    }

    void setupTextContainerLinearLayout() {
        textContainerLinearLayout = new LinearLayout(gismoContext);
        textContainerLinearLayout.setOrientation(LinearLayout.VERTICAL);
        fullContainerRelativeLayout.addView(textContainerLinearLayout);
        textContainerLinearLayout.getLayoutParams().height = RelativeLayout.LayoutParams.WRAP_CONTENT;
        textContainerLinearLayout.getLayoutParams().width = RelativeLayout.LayoutParams.WRAP_CONTENT;
    }

    void setupTitleTextView(int padding[], int size, String alignment) {
        TextView titleTextView = new TextView(gismoContext);
        textContainerLinearLayout.addView(titleTextView);
        setupCommonTextView(titleTextView, padding, size, alignment);
        titleTextView.setTag(Enums.ItemTag.Title);
    }

    void setupTimeTextView(int padding[], int size, String alignment) {
        TextView timeTextView = new TextView(gismoContext);
        textContainerLinearLayout.addView(timeTextView);
        setupCommonTextView(timeTextView, padding, size, alignment);
        timeTextView.setTag(Enums.ItemTag.Time);
    }

    private void setupCommonTextView(TextView textView, int[] padding, int size, String alignment) {
        textView.setMaxLines(1);
        textView.setHorizontallyScrolling(true);
        textView.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        textView.setMarqueeRepeatLimit(-1);
        textView.setSelected(true);
        textView.setTextSize(size);
        textView.setPadding(padding[0], padding[1], padding[2], padding[3]);
        textView.getLayoutParams().height = LinearLayout.LayoutParams.WRAP_CONTENT;
        textView.getLayoutParams().width = LinearLayout.LayoutParams.MATCH_PARENT;
        switch (alignment) {
            case "left": {
                textView.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
                break;
            }
            case "right": {
                textView.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_END);
                break;
            }
            case "center": {
                textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            }
        }
    }

    void setupColorImageView(int[] padding, String type, int[] manualDimensions, boolean[] isManualDimensions, int[] autoDimensions) {
        colorImageView = new ImageView(gismoContext);
        fullContainerRelativeLayout.addView(colorImageView);
        colorImageView.setTag(Enums.ItemTag.Image);
        GradientDrawable outlineDrawable = new GradientDrawable();
        ShapeDrawable shapeDrawable = new ShapeDrawable();
        if (type.equals("oval")) {
            shapeDrawable.setShape(new OvalShape());
            outlineDrawable.setShape(GradientDrawable.OVAL);
        } else {
            shapeDrawable.setShape(new RectShape());
            outlineDrawable.setShape(GradientDrawable.RECTANGLE);
        }
        shapeDrawable.setIntrinsicWidth(isManualDimensions[0] ? manualDimensions[0] : autoDimensions[0]);
        shapeDrawable.setIntrinsicHeight(isManualDimensions[1] ? manualDimensions[1] : autoDimensions[1]);
        colorImageView.setImageDrawable(new LayerDrawable(new Drawable[]{shapeDrawable, outlineDrawable}));
        colorImageView.getLayoutParams().height = RelativeLayout.LayoutParams.WRAP_CONTENT;
        colorImageView.getLayoutParams().width = RelativeLayout.LayoutParams.WRAP_CONTENT;
        colorImageView.setPadding(padding[0], padding[1], padding[2], padding[3]);
        colorImageView.setAdjustViewBounds(true);
    }

    /*
    -----------------------------------------------------------------------------------------------------------
    Text Position       Color Position      Stick Color         Status(T for text, C for color, OO for overlap)
    -----------------------------------------------------------------------------------------------------------

    left                left                true                |             |
                                                                |CT           |
                                                                |             |

                                            false               |             |
                                                                |OO           |
                                                                |             |

                        right               true                |             |
                                                                |TC           |
                                                                |             |

                                            false               |             |
                                                                |T           C|
                                                                |             |

                        up                  true                |C            |
                                                                |T            |
                                                                |             |

                                            false               |      C      |
                                                                |T            |
                                                                |             |

                        down                true                |             |
                                                                |T            |
                                                                |C            |

                                            false               |             |
                                                                |T            |
                                                                |      C      |

    right               left                true                |             |
                                                                |           CT|
                                                                |             |

                                            false               |             |
                                                                |C           T|
                                                                |             |

                        right               true                |             |
                                                                |           TC|
                                                                |             |

                                            false               |             |
                                                                |           OO|
                                                                |             |

                        up                  true                |            C|
                                                                |            T|
                                                                |             |

                                            false               |      C      |
                                                                |            T|
                                                                |             |

                        down                true                |             |
                                                                |            T|
                                                                |            C|

                                            false               |             |
                                                                |            T|
                                                                |      C      |

    center              left                true                |             |
                                                                |     CT      |
                                                                |             |

                                            false               |             |
                                                                |C     T      |
                                                                |             |

                        right               true                |             |
                                                                |      TC     |
                                                                |             |

                                            false               |             |
                                                                |      T     C|
                                                                |             |

                        up                  true                |      C      |
                                                                |      T      |
                                                                |             |

                                            false               |      C      |
                                                                |      T      |
                                                                |             |

                        down                true                |             |
                                                                |      T      |
                                                                |      C      |

                                            false               |             |
                                                                |      T      |
                                                                |      C      |

    -----------------------------------------------------------------------------------------------------------
     */


    // never look at this code. not even god knows what I wrote.
    void setupPositions(String textPosition, String colorPosition, boolean stickColor) {
        int textPositionRule;
        switch (textPosition) {
            case "left": {
                textPositionRule = RelativeLayout.ALIGN_PARENT_LEFT;
                break;
            }
            case "center": {
                textPositionRule = RelativeLayout.CENTER_HORIZONTAL;
                break;
            }
            case "right":
            default: {
                textPositionRule = RelativeLayout.ALIGN_PARENT_RIGHT;
            }
        }
        ((RelativeLayout.LayoutParams) textContainerLinearLayout.getLayoutParams()).addRule(textPositionRule);
        ((RelativeLayout.LayoutParams) textContainerLinearLayout.getLayoutParams()).addRule(RelativeLayout.CENTER_VERTICAL);
        if (colorImageView == null) {
            return;
        }
        textContainerLinearLayout.setId(View.generateViewId());
        colorImageView.setId(View.generateViewId());
        switch (colorPosition) {
            case "left": {
                if (!stickColor) {
                    ((RelativeLayout.LayoutParams) colorImageView.getLayoutParams()).addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                    ((RelativeLayout.LayoutParams) colorImageView.getLayoutParams()).addRule(RelativeLayout.CENTER_VERTICAL);
                    break;
                }
                if (textPosition.equals("left")) {
                    ((RelativeLayout.LayoutParams) colorImageView.getLayoutParams()).addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                    ((RelativeLayout.LayoutParams) textContainerLinearLayout.getLayoutParams()).removeRule(RelativeLayout.ALIGN_PARENT_LEFT);
                    ((RelativeLayout.LayoutParams) textContainerLinearLayout.getLayoutParams()).addRule(RelativeLayout.RIGHT_OF, colorImageView.getId());
                } else {
                    ((RelativeLayout.LayoutParams) colorImageView.getLayoutParams()).addRule(RelativeLayout.LEFT_OF, textContainerLinearLayout.getId());
                }
                ((RelativeLayout.LayoutParams) colorImageView.getLayoutParams()).addRule(RelativeLayout.CENTER_VERTICAL);
                break;
            }
            case "right": {
                if (!stickColor) {
                    ((RelativeLayout.LayoutParams) colorImageView.getLayoutParams()).addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                    ((RelativeLayout.LayoutParams) colorImageView.getLayoutParams()).addRule(RelativeLayout.CENTER_VERTICAL);
                    break;
                }
                if (textPosition.equals("right")) {
                    ((RelativeLayout.LayoutParams) colorImageView.getLayoutParams()).addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                    ((RelativeLayout.LayoutParams) textContainerLinearLayout.getLayoutParams()).removeRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                    ((RelativeLayout.LayoutParams) textContainerLinearLayout.getLayoutParams()).addRule(RelativeLayout.LEFT_OF, colorImageView.getId());
                } else {
                    ((RelativeLayout.LayoutParams) colorImageView.getLayoutParams()).addRule(RelativeLayout.RIGHT_OF, textContainerLinearLayout.getId());
                }
                ((RelativeLayout.LayoutParams) colorImageView.getLayoutParams()).addRule(RelativeLayout.CENTER_VERTICAL);
                break;
            }
            case "up": {
                if (stickColor) {
                    ((RelativeLayout.LayoutParams) colorImageView.getLayoutParams()).addRule(textPositionRule);
                }
                ((RelativeLayout.LayoutParams) colorImageView.getLayoutParams()).addRule(RelativeLayout.ALIGN_PARENT_TOP);
                ((RelativeLayout.LayoutParams) textContainerLinearLayout.getLayoutParams()).addRule(RelativeLayout.BELOW, colorImageView.getId());
                break;
            }
            case "down": {
                if (stickColor) {
                    ((RelativeLayout.LayoutParams) colorImageView.getLayoutParams()).addRule(textPositionRule);
                }
                ((RelativeLayout.LayoutParams) textContainerLinearLayout.getLayoutParams()).removeRule(RelativeLayout.CENTER_VERTICAL);
                ((RelativeLayout.LayoutParams) textContainerLinearLayout.getLayoutParams()).addRule(RelativeLayout.ALIGN_PARENT_TOP);
                ((RelativeLayout.LayoutParams) colorImageView.getLayoutParams()).addRule(RelativeLayout.BELOW, textContainerLinearLayout.getId());
                break;
            }
        }
    }
}
