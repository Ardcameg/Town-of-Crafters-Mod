package com.ardcameg.townofcrafters;

// 「ステータスの名前」「変化量」「バフ(true)かデバフ(false)か」をまとめる箱
public record RoleStat(String name, String value, boolean isPositive) {
}