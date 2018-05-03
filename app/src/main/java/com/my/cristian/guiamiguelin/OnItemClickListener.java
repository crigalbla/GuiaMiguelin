package com.my.cristian.guiamiguelin;

import domain.Restaurante;

/**
 * Created by Cristian on 02/03/2018.
 */

interface OnItemClickListener {
    void onItemClick(Restaurante restaurante);
    void onLongItemClick(Restaurante restaurante);
}

