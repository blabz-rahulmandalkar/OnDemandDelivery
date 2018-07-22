/**
 * @category Ask An Expert
 * @package    com.contus.views
 * @version 1.0
 *
 * @copyright Copyright (C) 2015 . All rights reserved.
 * @license http://www.apache.org/licenses/LICENSE-2.0
 */
package com.driverapp.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.EditText;

import com.driverapp.Utils;


/**
 * The Class CustomEditTextView.
 */
public class CustomEditText extends EditText {

    /** The m utils. */
    private Utils mUtils;

    /**
     * Instantiates a new custom edit text view.
     * 
     * @param context
     *            the context
     */
    public CustomEditText(Context context) {
        super(context);
    }

    /**
     * Instantiates a new custom edit text view.
     * 
     * @param context
     *            the context
     * @param attrs
     *            the attrs
     */
    public CustomEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        mUtils = new Utils();
        mUtils.init(this, context, attrs);
    }

    /**
     * Instantiates a new custom edit text view.
     * 
     * @param context
     *            the context
     * @param attrs
     *            the attrs
     * @param defStyle
     *            the def style
     */
    public CustomEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mUtils = new Utils();
        mUtils.init(this, context, attrs);
    }

}
