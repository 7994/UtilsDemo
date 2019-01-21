package com.example.mashuk.demo.utils;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.example.mashuk.demo.R;

import java.util.Stack;

/**
 * FragmentUtil.java : This class is used for Initialization, Add, Replace and remove the fragments
 *
 * @author : Harsh Patel
 * @version : 1.0.0
 * @Date : 02/06/2017
 * @Change History :
 * {Change ID:#} :
 */
public class FragmentUtil {

    private static String TAG = "FragmentUtil";

    public static FragmentUtil fragmentUtil;

    private FragmentManager mFragmentManager;
    private FragmentTransaction mFragmentTransaction;

    // Need to manage fragment transactions
    private Stack<Fragment> mFragmentCatch = new Stack<Fragment>();

    // Animation type to add, replace and remove fragment
    public enum ANIMATION_TYPE {
        SLIDE_UP_TO_DOWN, SLIDE_UP_TO_DOWN_BOUNCE, SLIDE_DOWN_TO_UP, SLIDE_LEFT_TO_RIGHT, SLIDE_RIGHT_TO_LEFT, NONE
    }

    // Initialize class variable to access all methods
    public FragmentUtil() {
        fragmentUtil = this;
    }

    // Return static instance of this class
    public static FragmentUtil getInstance() {
        return fragmentUtil;
    }

    /*
    * Get all required parameter to replace fragment
    * */
    public void replaceFragment(FragmentActivity activity, int containerId, Fragment fragment, String fragmentTag, ANIMATION_TYPE animationType) {
        // Initialize FragmentManager and FragmentTransaction
        mFragmentManager = activity.getSupportFragmentManager();
        mFragmentTransaction = mFragmentManager.beginTransaction();

        // Get last fragment
        Fragment frag = activity.getSupportFragmentManager().findFragmentById(containerId);


        if (frag != null) {
            // If last fragment and current fragment does not same, replace it nor leave as it is
            if (!frag.getTag().equals("" + fragmentTag)) {
                mFragmentCatch.clear(); // Clear fragment stack because after replacing fragment, no previous other fragments will be in stack

                // Catch animation type and set to FragmentTransaction
                switch (animationType) {
                    case SLIDE_UP_TO_DOWN:
                        mFragmentTransaction.setCustomAnimations(R.anim.enter_top, R.anim.exit_bottom);
                        break;
                    case SLIDE_UP_TO_DOWN_BOUNCE:
                        mFragmentTransaction.setCustomAnimations(R.anim.enter_top_bounce, R.anim.exit_bottom);
                        break;
                    case SLIDE_DOWN_TO_UP:
                        mFragmentTransaction.setCustomAnimations(R.anim.enter_bottom, R.anim.exit_top);
                        break;
                    case SLIDE_LEFT_TO_RIGHT:
                        mFragmentTransaction.setCustomAnimations(R.anim.enter_left, R.anim.exit_right);
                        break;
                    case SLIDE_RIGHT_TO_LEFT:
                        mFragmentTransaction.setCustomAnimations(R.anim.enter_right, R.anim.exit_left);
                        break;
                    default:
                        break;
                }

                // Replace fragment and add to Fragment stack
                mFragmentTransaction.replace(containerId, fragment, fragmentTag);
                mFragmentCatch.push(fragment);
                mFragmentTransaction.commitAllowingStateLoss();
            } else {
                AppLog.LogE(TAG, "Error : There is the same fragment as last fragment one. So, Fragment can not add/replace with new fragment");
            }
        } else {
            // If fragment container is empty, it replace fragment. Generally else is called when user start app first time.
            mFragmentCatch.clear();

            mFragmentTransaction.replace(containerId, fragment, fragmentTag);
            mFragmentCatch.push(fragment);
            mFragmentTransaction.commitAllowingStateLoss();
        }
    }

    /*
    * Get all required parameter to add fragment
    * */
    public void addFragment(FragmentActivity activity, int containerId, Fragment fragment, String fragmentTag, ANIMATION_TYPE animationType) {
        // Initialize FragmentManager and FragmentTransaction
        mFragmentManager = activity.getSupportFragmentManager();
        mFragmentTransaction = mFragmentManager.beginTransaction();

        // Get last fragment
        Fragment frag = activity.getSupportFragmentManager().findFragmentById(containerId);

        if (frag != null) {
            // If last fragment and current fragment does not same, add it nor leave as it is
            if (!frag.getTag().equals("" + fragmentTag)) {

                // Catch animation type and set to FragmentTransaction
                switch (animationType) {
                    case SLIDE_UP_TO_DOWN:
                        mFragmentTransaction.setCustomAnimations(R.anim.enter_top, R.anim.exit_bottom);
                        break;
                    case SLIDE_UP_TO_DOWN_BOUNCE:
                        mFragmentTransaction.setCustomAnimations(R.anim.enter_top_bounce, R.anim.exit_bottom);
                        break;
                    case SLIDE_DOWN_TO_UP:
                        mFragmentTransaction.setCustomAnimations(R.anim.enter_bottom, R.anim.exit_top);
                        break;
                    case SLIDE_LEFT_TO_RIGHT:
                        mFragmentTransaction.setCustomAnimations(R.anim.enter_left, R.anim.exit_right);
                        break;
                    case SLIDE_RIGHT_TO_LEFT:
                        mFragmentTransaction.setCustomAnimations(R.anim.enter_right, R.anim.exit_left);
                        break;
                    default:
                        break;
                }

                // Add new fragment and also add to Fragment stack
                mFragmentTransaction.add(containerId, fragment, fragmentTag);
                mFragmentCatch.push(fragment);
                mFragmentTransaction.commitAllowingStateLoss();
            } else {
                //LogUtil.e(TAG,"Error : " + mFragmentCatch.lastElement().getTag() + " is the same fragment as last fragment.  So, Fragment can not add/replace new fragment");
            }
        } else {
            // If fragment container is empty, it add fragment. Generally else is called when user start app first time.
            //mFragmentTransaction.setCustomAnimations(R.anim.enter_right, R.anim.exit_left);
            mFragmentTransaction.add(containerId, fragment, fragmentTag);
            mFragmentCatch.push(fragment);
            mFragmentTransaction.commitAllowingStateLoss();
        }
    }

    /*
    * Get all required parameter to remove fragment
    * */
    public void removeFragment(FragmentActivity activity, ANIMATION_TYPE animationType) {
        // Initialize FragmentManager and FragmentTransaction
        mFragmentManager = activity.getSupportFragmentManager();
        mFragmentTransaction = mFragmentManager.beginTransaction();

        // remove fragment if there is more than or equals one fragment in stack
        if (mFragmentCatch.size() >= 1) {
            switch (animationType) {
                case SLIDE_UP_TO_DOWN:
                    mFragmentTransaction.setCustomAnimations(R.anim.enter_top, R.anim.exit_bottom);
                    break;
                case SLIDE_UP_TO_DOWN_BOUNCE:
                    mFragmentTransaction.setCustomAnimations(R.anim.enter_top_bounce, R.anim.exit_bottom);
                    break;
                case SLIDE_DOWN_TO_UP:
                    mFragmentTransaction.setCustomAnimations(R.anim.enter_bottom, R.anim.exit_top);
                    break;
                case SLIDE_LEFT_TO_RIGHT:
                    mFragmentTransaction.setCustomAnimations(R.anim.enter_left, R.anim.exit_right);
                    break;
                case SLIDE_RIGHT_TO_LEFT:
                    mFragmentTransaction.setCustomAnimations(R.anim.enter_right, R.anim.exit_left);
                    break;
                default:
                    break;
            }

            // Remove fragment and erase entry from Fragment stack
            mFragmentTransaction.remove(mFragmentCatch.pop());
            mFragmentTransaction.commitAllowingStateLoss();
        } else if (mFragmentCatch.size() == 1) {
            AppLog.LogE(TAG, "Error : " + mFragmentCatch.lastElement().getTag() + " is a last fragment in the stack.");
        } else {
            AppLog.LogE(TAG, "Error : No fragment in the stack. ");
        }
    }

    /*
    * Get all required parameter to remove fragment
    * */
    public void removeFragment(FragmentActivity activity, Fragment fragment, ANIMATION_TYPE animationType) {
        // Initialize FragmentManager and FragmentTransaction
        mFragmentManager = activity.getSupportFragmentManager();
        mFragmentTransaction = mFragmentManager.beginTransaction();

        // remove fragment if there is more than or equals one fragment in stack
        if (mFragmentCatch.size() >= 1) {
            switch (animationType) {
                case SLIDE_UP_TO_DOWN:
                    mFragmentTransaction.setCustomAnimations(R.anim.enter_top, R.anim.exit_bottom);
                    break;
                case SLIDE_UP_TO_DOWN_BOUNCE:
                    mFragmentTransaction.setCustomAnimations(R.anim.enter_top_bounce, R.anim.exit_bottom);
                    break;
                case SLIDE_DOWN_TO_UP:
                    mFragmentTransaction.setCustomAnimations(R.anim.enter_bottom, R.anim.exit_top);
                    break;
                case SLIDE_LEFT_TO_RIGHT:
                    mFragmentTransaction.setCustomAnimations(R.anim.enter_left, R.anim.exit_right);
                    break;
                case SLIDE_RIGHT_TO_LEFT:
                    mFragmentTransaction.setCustomAnimations(R.anim.enter_right, R.anim.exit_left);
                    break;
                default:
                    break;
            }

            // Remove fragment and erase entry from Fragment stack
            try {
                mFragmentTransaction.remove(fragment);
                mFragmentCatch.remove(getFragmentPositionFromBottom(fragment));
                mFragmentTransaction.commitAllowingStateLoss();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (mFragmentCatch.size() == 1) {
            //LogUtil.e(TAG,"Error : " + mFragmentCatch.lastElement().getTag() + " is a last fragment in the stack.");
        } else {
            //LogUtil.e(TAG,"Error : No fragment in the stack. ");
        }
    }

    /*
    *  It removes all fragments upto fragment whose tag is defined as "lastFragmentTag"
    *  It does not remove fragments whose tag is defined as "lastFragmentTag".
    * */
    public void removeAllFragmentsUpto(FragmentActivity activity, String lastFragmentTag, ANIMATION_TYPE animationType) {
        try {
            Fragment lastFragment = activity.getSupportFragmentManager().findFragmentByTag("" + lastFragmentTag);

            if (lastFragment != null) {
                int totalStackCount = getTotalFragmentCount();
                int posOfRemoveUpto = getFragmentPositionFromBottom(lastFragment);

                for (int i = (totalStackCount - 1); i > posOfRemoveUpto; i--) {
                    Fragment fragment = getFragment(i);
                    removeFragment(activity, fragment, animationType);
                }
            } else {
                throw new IllegalArgumentException("Fragment not found for given tag \"" + lastFragmentTag + "\".");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
    *  It removes all fragments between fragment whose tag is defined as "fromFragmentTag" and "toFragmentTag".
    *  It does not remove fragments whose tag is defined as "fromFragmentTag" and "toFragmentTag".
    * */
    public void removeAllFragmentsBetween(FragmentActivity activity, String fromFragmentTag, String toFragmentTag, ANIMATION_TYPE animationType) {
        try {
            Fragment fromFragment = activity.getSupportFragmentManager().findFragmentByTag("" + fromFragmentTag);
            Fragment toFragment = activity.getSupportFragmentManager().findFragmentByTag("" + toFragmentTag);

            if (fromFragment != null && toFragment != null) {
                int fromFragPos = getFragmentPositionFromBottom(fromFragment);
                int toFragPos = getFragmentPositionFromBottom(toFragment);

                if (fromFragPos < toFragPos) {
                    int tempFragPos = fromFragPos;
                    fromFragPos = toFragPos;
                    toFragPos = tempFragPos;
                }

                for (int i = (fromFragPos - 1); i > toFragPos; i--) {
                    Fragment fragment = getFragment(i);
                    removeFragment(activity, fragment, animationType);
                }
            } else {
                throw new IllegalArgumentException("Either given fragment tag \"" + fromFragmentTag + "\" or \"" + toFragmentTag + "\" not found. ");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
    *  It removes all fragments upto fragment whose tag is defined as "lastFragmentTag"
    *  It does not remove fragments whose tag is defined as "lastFragmentTag".
    *  It also skip "exceptFragment" fragment.
    * */
    public void removeAllFragmentsUptoAndExcept(FragmentActivity activity, String lastFragmentTag, String exceptFragment, ANIMATION_TYPE animationType) {
        try {
            Fragment lastFragment = activity.getSupportFragmentManager().findFragmentByTag("" + lastFragmentTag);

            if (lastFragment != null) {
                int totalStackCount = getTotalFragmentCount();
                int posOfRemoveUpto = getFragmentPositionFromBottom(lastFragment);

                for (int i = (totalStackCount - 1); i > posOfRemoveUpto; i--) {
                    Fragment fragment = getFragment(i);

                    if (!(fragment.getTag()).equals(exceptFragment)) {
                        removeFragment(activity, fragment, animationType);
                    }
                }
            } else {
                throw new IllegalArgumentException("Fragment not found for given tag \"" + lastFragmentTag + "\". ");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
    *  It returns total number of fragments in a stack
    * */
    public int getTotalFragmentCount() {
        return mFragmentCatch.size();
    }

    /*
    *  It returns fragment at selected position
    * */
    public Fragment getFragment(int pos) {
        return mFragmentCatch.get(pos);
    }

    /*
    *  stack.search(object) returns the position from top of the stack
    * */
    public int getFragmentPositionFromTop(Fragment fragment) {
        return mFragmentCatch.search(fragment);
    }

    /*
    *  stack - stack.search(object) returns the position from bottom of the stack
    * */
    public int getFragmentPositionFromBottom(Fragment fragment) {
        return ((mFragmentCatch.size()) - getFragmentPositionFromTop(fragment));
    }

    public boolean hasFragmentByTag(FragmentActivity activity, String fragmentTag) {
        Fragment fragment = activity.getSupportFragmentManager().findFragmentByTag("" + fragmentTag);

        if (fragment != null) {
            return true;
        } else {
            return false;
        }
    }

    public void restartFragment(FragmentActivity activity, String fragmentTag) {
        Fragment fragment = activity.getSupportFragmentManager().findFragmentByTag("" + fragmentTag);

        if (fragment != null) {
            activity.getSupportFragmentManager().beginTransaction()
                    .detach(fragment)
                    .attach(fragment)
                    .commit();
        }
    }
}