package com.mukunda.dungeons;

public enum CooldownType {
	NONE,
	DAY,	// RESETS EVERY DAY
	WEEK,	// RESETS EVERY WEEK
	MANUAL; // RESETS WHEN AN ADMIN PUSHES A BUTTON
	
	int value;
	
	public static CooldownType fromString( String type ) {
		if( type == null ) {
			return NONE;
		}
		if( type.equalsIgnoreCase("none") ) {
			return NONE;
		} else if( type.equalsIgnoreCase("day") ) {
			return DAY;
		} else if( type.equalsIgnoreCase("week") ) {
			return WEEK;
		} else if( type.equalsIgnoreCase("manual") ) {
			return MANUAL;
		}
		return NONE;
	}
	
	public String toString( ) {
		
		switch( this  ) {
		case NONE:
			return "none";
		case DAY:
			return "day";
		case WEEK:
			return "week";
		case MANUAL:
			return "manual";
		}
		return "none";
	}
	
}
