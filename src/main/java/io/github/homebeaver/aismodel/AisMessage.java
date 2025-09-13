package io.github.homebeaver.aismodel;

import com.google.gson.annotations.SerializedName;

import io.swagger.annotations.ApiModelProperty;

public class AisMessage {

	public static final String SERIALIZED_NAME_MESSAGE_I_D = "MessageID";
	@SerializedName(SERIALIZED_NAME_MESSAGE_I_D)
	/*
	 * AisMessage1 extends AisPositionMessage or 
	 * AisMessage2 extends AisMessage1 or
	 * AisMessage3 extends AisPositionMessage 
	 * <p> 
	 * AisMessage3 : Special position report, response to interrogation;
	 * (Class A shipborne mobile equipment) 
	 * <p> 
	 * 4 Base station report 
	 * <p> 
	 * 5 Static and voyage related data
	 */
	private Integer messageID;

	public static final String SERIALIZED_NAME_REPEAT_INDICATOR = "RepeatIndicator";
	@SerializedName(SERIALIZED_NAME_REPEAT_INDICATOR)
	private Integer repeatIndicator;

	/**
	 * R-REC-M.1371-1: 3.3.7.2.1 User ID 
	 * <p>
	 * The user ID should be the MMSI. The MMSI is 30 bits long. 
	 * The first 9 digits (most significant digits) should be used only. 
	 * Recommendation ITU-R M.1083 should not be applied with respect to the 10th digit 
	 * (least significant digit).
	 */
	public static final String SERIALIZED_NAME_USER_I_D = "UserID";
	@SerializedName(SERIALIZED_NAME_USER_I_D)
	private Integer userID;

	public static final String SERIALIZED_NAME_VALID = "Valid";
	@SerializedName(SERIALIZED_NAME_VALID)
	private Boolean valid;

	AisMessage() {}

	public AisMessage messageID(Integer messageID) {
		this.messageID = messageID;
		return this;
	}

	/**
	 * Get messageID
	 * 
	 * @return messageID
	 **/
	@javax.annotation.Nonnull
	@ApiModelProperty(required = true, value = "")
	public Integer getMessageID() {
		return messageID;
	}

	public void setMessageID(Integer messageID) {
		this.messageID = messageID;
	}

	public AisMessage repeatIndicator(Integer repeatIndicator) {
		this.repeatIndicator = repeatIndicator;
		return this;
	}

	/**
	 * Get repeatIndicator
	 * 
	 * @return repeatIndicator
	 **/
	@javax.annotation.Nonnull
	@ApiModelProperty(required = true, value = "")
	public Integer getRepeatIndicator() {
		return repeatIndicator;
	}

	public void setRepeatIndicator(Integer repeatIndicator) {
		this.repeatIndicator = repeatIndicator;
	}

	public AisMessage userID(Integer userID) {
		this.userID = userID;
		return this;
	}

	/**
	 * Get userID
	 * 
	 * @return userID
	 **/
	@javax.annotation.Nonnull
	@ApiModelProperty(required = true, value = "")
	public Integer getUserID() {
		return userID;
	}

	public void setUserID(Integer userID) {
		this.userID = userID;
	}

	public AisMessage valid(Boolean valid) {
		this.valid = valid;
		return this;
	}

	/**
	 * Get valid
	 * 
	 * @return valid
	 **/
	@javax.annotation.Nonnull
	@ApiModelProperty(required = true, value = "")

	public Boolean getValid() {
		return valid;
	}

	public void setValid(Boolean valid) {
		this.valid = valid;
	}

}
