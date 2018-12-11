//<MStar Software>
//******************************************************************************
// MStar Software
// Copyright (c) 2010 - 2012 MStar Semiconductor, Inc. All rights reserved.
// All software, firmware and related documentation herein ("MStar Software") are
// intellectual property of MStar Semiconductor, Inc. ("MStar") and protected by
// law, including, but not limited to, copyright law and international treaties.
// Any use, modification, reproduction, retransmission, or republication of all
// or part of MStar Software is expressly prohibited, unless prior written
// permission has been granted by MStar.
//
// By accessing, browsing and/or using MStar Software, you acknowledge that you
// have read, understood, and agree, to be bound by below terms ("Terms") and to
// comply with all applicable laws and regulations:
//
// 1. MStar shall retain any and all right, ownership and interest to MStar
//    Software and any modification/derivatives thereof.
//    No right, ownership, or interest to MStar Software and any
//    modification/derivatives thereof is transferred to you under Terms.
//
// 2. You understand that MStar Software might include, incorporate or be
//    supplied together with third party's software and the use of MStar
//    Software may require additional licenses from third parties.
//    Therefore, you hereby agree it is your sole responsibility to separately
//    obtain any and all third party right and license necessary for your use of
//    such third party's software.
//
// 3. MStar Software and any modification/derivatives thereof shall be deemed as
//    MStar's confidential information and you agree to keep MStar's
//    confidential information in strictest confidence and not disclose to any
//    third party.
//
// 4. MStar Software is provided on an "AS IS" basis without warranties of any
//    kind. Any warranties are hereby expressly disclaimed by MStar, including
//    without limitation, any warranties of merchantability, non-infringement of
//    intellectual property rights, fitness for a particular purpose, error free
//    and in conformity with any international standard.  You agree to waive any
//    claim against MStar for any loss, damage, cost or expense that you may
//    incur related to your use of MStar Software.
//    In no event shall MStar be liable for any direct, indirect, incidental or
//    consequential damages, including without limitation, lost of profit or
//    revenues, lost or damage of data, and unauthorized system use.
//    You agree that this Section 4 shall still apply without being affected
//    even if MStar Software has been modified by MStar in accordance with your
//    request or instruction for your use, except otherwise agreed by both
//    parties in writing.
//
// 5. If requested, MStar may from time to time provide technical supports or
//    services in relation with MStar Software to you for your use of
//    MStar Software in conjunction with your or your customer's product
//    ("Services").
//    You understand and agree that, except otherwise agreed by both parties in
//    writing, Services are provided on an "AS IS" basis and the warranty
//    disclaimer set forth in Section 4 above shall apply.
//
// 6. Nothing contained herein shall be construed as by implication, estoppels
//    or otherwise:
//    (a) conferring any license or right to use MStar name, trademark, service
//        mark, symbol or any other identification;
//    (b) obligating MStar or any of its affiliates to furnish any person,
//        including without limitation, you and your customers, any assistance
//        of any kind whatsoever, or any information; or
//    (c) conferring any license or right under any intellectual property right.
//
// 7. These terms shall be governed by and construed in accordance with the laws
//    of Taiwan, R.O.C., excluding its conflict of law rules.
//    Any and all dispute arising out hereof or related hereto shall be finally
//    settled by arbitration referred to the Chinese Arbitration Association,
//    Taipei in accordance with the ROC Arbitration Law and the Arbitration
//    Rules of the Association by three (3) arbitrators appointed in accordance
//    with the said Rules.
//    The place of arbitration shall be in Taipei, Taiwan and the language shall
//    be English.
//    The arbitration award shall be final and binding to both parties.
//
//******************************************************************************
//<MStar Software>

package com.example.benq4kdemo.photo2;

import com.example.benq4kdemo.R;

import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.TextView;

public class ImagePlayerViewHolder {
    private ImagePlayerActivity mPhotoPlayerActivity;

    protected ImagePlayerSurfaceView mSurfaceView;

    protected ImageView bt_photoPre;

    protected ImageView bt_photoPlay;

    protected ImageView bt_photoNext;

    protected ImageView bt_photoEnlarge;

    protected ImageView bt_photoNarrow;

    protected ImageView bt_photoTLeft;

    protected ImageView bt_photoTRight;

    protected TextView current_num;

    protected TextView total_num;

    protected TextView photo_name;

    // playing setting name
    public static int[] playSettingName = { R.string.picture_animation_setting,
    R.string.picture_playing_time};


    public ImagePlayerViewHolder(ImagePlayerActivity activity) {
        this.mPhotoPlayerActivity = activity;
    }

    /**
     * Get button.
     */
    void findViews() {
        photo_name = (TextView) mPhotoPlayerActivity.findViewById(R.id.photo_name_display);
        mSurfaceView = (ImagePlayerSurfaceView) mPhotoPlayerActivity.findViewById(R.id.imageSurfaceView);
        bt_photoPre = (ImageView) mPhotoPlayerActivity.findViewById(R.id.player_previous);
        bt_photoPlay = (ImageView) mPhotoPlayerActivity.findViewById(R.id.photo_play);
        bt_photoPlay.setBackgroundResource(R.drawable.player_icon_play_focus);
        bt_photoNext = (ImageView) mPhotoPlayerActivity.findViewById(R.id.photo_next);
        bt_photoEnlarge = (ImageView) mPhotoPlayerActivity.findViewById(R.id.photo_enlarge);
        bt_photoNarrow = (ImageView) mPhotoPlayerActivity.findViewById(R.id.photo_narrow);
        bt_photoTLeft = (ImageView) mPhotoPlayerActivity.findViewById(R.id.photo_turn_left);
        bt_photoTRight = (ImageView) mPhotoPlayerActivity.findViewById(R.id.photo_turn_right);

    }

    public void setOnClickListener(OnClickListener listener) {
        if (listener != null) {
            bt_photoPre.setOnClickListener(listener);
            bt_photoPlay.setOnClickListener(listener);
            bt_photoNext.setOnClickListener(listener);
            bt_photoEnlarge.setOnClickListener(listener);
            bt_photoNarrow.setOnClickListener(listener);
            bt_photoTLeft.setOnClickListener(listener);
            bt_photoTRight.setOnClickListener(listener);
            mSurfaceView.setOnClickListener(listener);
        }
    }

    public void setAllUnSelect(boolean pptPlaying, boolean isPlaying) {
        setPhotoPreSelect(false);
        setPhotoPlaySelect(false, pptPlaying);
        setPhotoNextSelect(false);
        setPhotoEnlargeSelect(false);
        setPhotoNarrowSelect(false);
        setPhotoTurnLeftSelect(false);
        setPhotoTurnRightSelect(false);
    }

    public void setPhotoPreSelect(boolean bSelect) {
        if (bSelect) {
            bt_photoPre.setFocusable(true);
            bt_photoPre.setBackgroundResource(R.drawable.player_icon_previous_focus);
        } else {
            bt_photoPre.setFocusable(false);
            bt_photoPre.setBackgroundResource(R.drawable.player_icon_previous);
        }
    }

    public void setPhotoPlaySelect(boolean bSelect, boolean isPlaying) {
        bt_photoPlay.setFocusable(bSelect);
        if (bSelect) {
            if (isPlaying)
                bt_photoPlay.setBackgroundResource(R.drawable.player_icon_pause_focus);
            else
                bt_photoPlay.setBackgroundResource(R.drawable.player_icon_play_focus);
        } else {
            if (isPlaying)
                bt_photoPlay.setBackgroundResource(R.drawable.player_icon_pause);
            else
                bt_photoPlay.setBackgroundResource(R.drawable.player_icon_play);
        }
    }

    public void setPhotoNextSelect(boolean bSelect) {
        if (bSelect) {
            bt_photoNext.setFocusable(true);
            bt_photoNext.setBackgroundResource(R.drawable.player_icon_next_focus);
        } else {
            bt_photoNext.setFocusable(false);
            bt_photoNext.setBackgroundResource(R.drawable.player_icon_next);
        }
    }

    public void setPhotoEnlargeSelect(boolean bSelect) {
        if (bSelect) {
            bt_photoEnlarge.setFocusable(true);
            bt_photoEnlarge.setBackgroundResource(R.drawable.player_icon_amplification_focus);
        } else {
            bt_photoEnlarge.setFocusable(false);
            bt_photoEnlarge.setBackgroundResource(R.drawable.player_icon_amplification);
        }
    }

    public void setPhotoNarrowSelect(boolean bSelect) {
        if (bSelect) {
            bt_photoNarrow.setFocusable(true);
            bt_photoNarrow.setBackgroundResource(R.drawable.player_icon_narrow_focus);
        } else {
            bt_photoNarrow.setFocusable(false);
            bt_photoNarrow.setBackgroundResource(R.drawable.player_icon_narrow);
        }
    }

    public void setPhotoTurnLeftSelect(boolean bSelect) {
        if (bSelect) {
            bt_photoTLeft.setFocusable(true);
            bt_photoTLeft.setBackgroundResource(R.drawable.player_icon_left_focus);
        } else {
            bt_photoTLeft.setFocusable(false);
            bt_photoTLeft.setBackgroundResource(R.drawable.player_icon_left);
        }
    }

    public void setPhotoTurnRightSelect(boolean bSelect) {
        if (bSelect) {
            bt_photoTRight.setFocusable(true);
            bt_photoTRight.setBackgroundResource(R.drawable.player_icon_right_focus);
        } else {
            bt_photoTRight.setFocusable(false);
            bt_photoTRight.setBackgroundResource(R.drawable.player_icon_right);
        }
    }

}
