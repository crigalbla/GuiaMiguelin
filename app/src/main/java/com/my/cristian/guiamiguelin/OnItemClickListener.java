package com.my.cristian.guiamiguelin;

import domain.Restaurant;

/**
 * Created by Cristian on 02/03/2018.
 */

interface OnItemClickListener {
    void onItemClick(Restaurant restaurante);
    void onLongItemClick(Restaurant restaurante);
}

