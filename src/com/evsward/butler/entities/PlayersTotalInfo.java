package com.evsward.butler.entities;

import java.util.ArrayList;

public class PlayersTotalInfo {
	private int totalRegedPlayer;
	private int totalRegedPlayerEdit;
	private int totalChip;
	private int totalChipEdit;
	private ArrayList<PlayerInfo> playerInfos;

	public ArrayList<PlayerInfo> getPlayerInfos() {
		return playerInfos;
	}

	public void setPlayerInfos(ArrayList<PlayerInfo> playerInfos) {
		this.playerInfos = playerInfos;
	}

	public int getTotalRegedPlayer() {
		return totalRegedPlayer;
	}

	public void setTotalRegedPlayer(int totalRegedPlayer) {
		this.totalRegedPlayer = totalRegedPlayer;
	}

	public int getTotalRegedPlayerEdit() {
		return totalRegedPlayerEdit;
	}

	public void setTotalRegedPlayerEdit(int totalRegedPlayerEdit) {
		this.totalRegedPlayerEdit = totalRegedPlayerEdit;
	}

	public int getTotalChip() {
		return totalChip;
	}

	public void setTotalChip(int totalChip) {
		this.totalChip = totalChip;
	}

	public int getTotalChipEdit() {
		return totalChipEdit;
	}

	public void setTotalChipEdit(int totalChipEdit) {
		this.totalChipEdit = totalChipEdit;
	}

}
