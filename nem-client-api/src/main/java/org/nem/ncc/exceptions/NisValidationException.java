package org.nem.ncc.exceptions;

/**
 * Generated error codes for nis validation errors.
 */
public enum NisValidationException implements ValueBasedEnum {
	//region general

	/**
	 * Validation failed for an unknown reason.
	 */
	TRANSACTION_REJECTED_UNKNOWN(1200 + 2),

	/**
	 * Validation failed because the deadline passed.
	 */
	TRANSACTION_REJECTED_PAST_DEADLINE(1200 + 3),

	/**
	 * Validation failed because the deadline is too far in the future.
	 */
	TRANSACTION_REJECTED_FUTURE_DEADLINE(1200 + 4),

	/**
	 * Validation failed because the account had an insufficient balance.
	 */
	TRANSACTION_REJECTED_INSUFFICIENT_BALANCE(1200 + 5),

	/**
	 * Validation failed because the message is too large.
	 */
	TRANSACTION_REJECTED_MESSAGE_TOO_LARGE(1200 + 6),

	/**
	 * Validation failed because the transaction hash is already known.
	 */
	TRANSACTION_REJECTED_HASH_EXISTS(1200 + 7),

	/**
	 * Validation failed because the verification of the signature failed.
	 */
	TRANSACTION_REJECTED_SIGNATURE_NOT_VERIFIABLE(1200 + 8),

	/**
	 * Validation failed because the time stamp is too far in the past.
	 */
	TRANSACTION_REJECTED_TIMESTAMP_TOO_FAR_IN_PAST(1200 + 9),

	/**
	 * Validation failed because the time stamp is too far in the future.
	 */
	TRANSACTION_REJECTED_TIMESTAMP_TOO_FAR_IN_FUTURE(1200 + 10),

	/**
	 * Validation failed because the block had an ineligible signer.
	 * This usually occurs when remote harvesting is in the process of being activated or deactivated.
	 */
	TRANSACTION_REJECTED_INELIGIBLE_BLOCK_SIGNER(1200 + 11),

	/**
	 * Validation failed because the entity cannot be used because the nodes are out of sync.
	 */
	TRANSACTION_REJECTED_ENTITY_UNUSABLE_OUT_OF_SYNC(1200 + 12),

	/**
	 * Validation failed because the chain score is inferior to our score.
	 */
	TRANSACTION_REJECTED_CHAIN_SCORE_INFERIOR(1200 + 13),

	/**
	 * Validation failed because the chain could not be validated.
	 */
	TRANSACTION_REJECTED_CHAIN_INVALID(1200 + 14),

	/**
	 * Validation failed because there are too many transactions in a block.
	 */
	TRANSACTION_REJECTED_TOO_MANY_TRANSACTIONS(1200 + 15),

	/**
	 * Validation failed because a block contained a self-signed transaction.
	 */
	TRANSACTION_REJECTED_SELF_SIGNED_TRANSACTION(1200 + 16),

	/**
	 * Validation failed because a transaction has an insufficient fee.
	 */
	TRANSACTION_REJECTED_INSUFFICIENT_FEE(1200 + 17),

	/**
	 * Validation failed because a transaction originated from the nemesis account after the nemesis block.
	 */
	TRANSACTION_REJECTED_NEMESIS_ACCOUNT_TRANSACTION_AFTER_NEMESIS_BLOCK(1200 + 18),

	/**
	 * Transaction was rejected because the debtor is not allowed to put another transaction into the cache.
	 */
	TRANSACTION_REJECTED_TRANSACTION_CACHE_TOO_FULL(1200 + 19),

	/**
	 * Entity was rejected because it has the wrong network specified.
	 */
	TRANSACTION_REJECTED_WRONG_NETWORK(1200 + 20),

	/**
	 * Block was rejected because it was harvested by a blocked account (typically a reserved NEM fund).
	 */
	TRANSACTION_REJECTED_CANNOT_HARVEST_FROM_BLOCKED_ACCOUNT(1200 + 21),

	//endregion

	//region importance 6x

	/**
	 * Validation failed because remote harvesting account has a pre-existing balance transfer.
	 */
	TRANSACTION_REJECTED_DESTINATION_ACCOUNT_HAS_PREEXISTING_BALANCE_TRANSFER(1200 + 62),

	/**
	 * Validation failed because previous importance transfer change is in progress.
	 */
	TRANSACTION_REJECTED_IMPORTANCE_TRANSFER_IN_PROGRESS(1200 + 63),

	/**
	 * Validation failed because importance transfer activation was attempted while already activated.
	 */
	TRANSACTION_REJECTED_IMPORTANCE_TRANSFER_NEEDS_TO_BE_DEACTIVATED(1200 + 64),

	/**
	 * Validation failed because importance transfer deactivation was attempted while already deactivated.
	 */
	TRANSACTION_REJECTED_IMPORTANCE_TRANSFER_IS_NOT_ACTIVE(1200 + 65),

	/**
	 * Validation failed because transaction is using remote account in an improper way.
	 */
	TRANSACTION_REJECTED_TRANSACTION_NOT_ALLOWED_FOR_REMOTE(1200 + 66),

	//endregion

	//region multisig 7x 8x

	/**
	 * Validation failed because signer is not a cosigner of given multisig account.
	 */
	TRANSACTION_REJECTED_MULTISIG_NOT_A_COSIGNER(1200 + 71),

	/**
	 * Validation failed because the cosignatories attached to a multisig transaction were invalid.
	 */
	TRANSACTION_REJECTED_MULTISIG_INVALID_COSIGNERS(1200 + 72),

	/**
	 * Validation failed because a multisig signature was not associated with any known multisig transaction.
	 */
	TRANSACTION_REJECTED_MULTISIG_NO_MATCHING_MULTISIG(1200 + 73),

	/**
	 * Validation failed because multisig account tried to make transaction that is not allowed.
	 */
	TRANSACTION_REJECTED_TRANSACTION_NOT_ALLOWED_FOR_MULTISIG(1200 + 74),

	/**
	 * Validation failed because signer is already a cosigner of given multisig account.
	 */
	TRANSACTION_REJECTED_MULTISIG_ALREADY_A_COSIGNER(1200 + 75),

	/**
	 * Validation failed because a multisig signature is attached to an incorrect multisig transaction.
	 */
	TRANSACTION_REJECTED_MULTISIG_MISMATCHED_SIGNATURE(1200 + 76),

	/**
	 * Validation failed because a multisig modification contained multiple deletes.
	 */
	TRANSACTION_REJECTED_MULTISIG_MODIFICATION_MULTIPLE_DELETES(1200 + 77),

	/**
	 * Validation failed because a multisig modification contained redundant cosignatory modifications.
	 */
	TRANSACTION_REJECTED_MULTISIG_MODIFICATION_REDUNDANT_MODIFICATIONS(1200 + 78),

	/**
	 * Validation failed because conflicting multisig modification is present.
	 */
	TRANSACTION_REJECTED_CONFLICTING_MULTISIG_MODIFICATION(1200 + 79),

	/**
	 * Validation failed because a multisig modification would result in a multisig account having too many cosigners.
	 */
	TRANSACTION_REJECTED_TOO_MANY_MULTISIG_COSIGNERS(1200 + 80),

	/**
	 * Validation failed because a multisig modification would result in a multisig account being a cosigner.
	 */
	TRANSACTION_REJECTED_MULTISIG_ACCOUNT_CANNOT_BE_COSIGNER(1200 + 81),

	/**
	 * Validation failed because the minimum number of cosignatories is larger than the number of cosignatories.
	 */
	TRANSACTION_REJECTED_MULTISIG_MIN_COSIGNATORIES_OUT_OF_RANGE(1200 + 82),

	/**
	 * Validation failed because V2 multisig aggregate modification transactions are not allowed before the fork height.
	 */
	TRANSACTION_REJECTED_MULTISIG_V2_AGGREGATE_MODIFICATION_BEFORE_FORK(1200 + 83),

	//endregion

	//region block chain validator 10x 11x

	/**
	 * Validation failed because received chain has too many blocks.
	 */
	TRANSACTION_REJECTED_MAX_CHAIN_SIZE_EXCEEDED(1200 + 101),

	/**
	 * Validation failed because a block was received with an unexpected height.
	 */
	TRANSACTION_REJECTED_BLOCK_UNEXPECTED_HEIGHT(1200 + 102),

	/**
	 * Validation failed because an unverifiable block was received.
	 */
	TRANSACTION_REJECTED_BLOCK_UNVERIFIABLE(1200 + 103),

	/**
	 * Validation failed because a block was received that is not a hit.
	 */
	TRANSACTION_REJECTED_BLOCK_NOT_HIT(1200 + 104),

	/**
	 * Validation failed because an unverifiable transaction was received.
	 */
	TRANSACTION_REJECTED_TRANSACTION_UNVERIFIABLE(1200 + 105),

	/**
	 * Validation failed because an incoming chain contained a transaction more than once.
	 */
	TRANSACTION_REJECTED_TRANSACTION_DUPLICATE_IN_CHAIN(1200 + 106),

	//endregion

	//region namespace 12x

	/**
	 * Validation failed because the namespace is unknown.
	 */
	TRANSACTION_REJECTED_NAMESPACE_UNKNOWN(1200 + 121),

	/**
	 * Validation failed because the namespace already exists.
	 */
	TRANSACTION_REJECTED_NAMESPACE_ALREADY_EXISTS(1200 + 122),

	/**
	 * Validation failed because the namespace has expired.
	 */
	TRANSACTION_REJECTED_NAMESPACE_EXPIRED(1200 + 123),

	/**
	 * Validation failed because the transaction signer is not the owner of the namespace.
	 */
	TRANSACTION_REJECTED_NAMESPACE_OWNER_CONFLICT(1200 + 124),

	/**
	 * Validation failed because the name for the namespace is invalid.
	 */
	TRANSACTION_REJECTED_NAMESPACE_INVALID_NAME(1200 + 125),

	/**
	 * Validation failed because the specified namespace lessor is invalid.
	 */
	TRANSACTION_REJECTED_NAMESPACE_INVALID_LESSOR(1200 + 126),

	/**
	 * Validation failed because the specified rental fee is invalid.
	 */
	TRANSACTION_REJECTED_NAMESPACE_INVALID_RENTAL_FEE(1200 + 127),

	/**
	 * Validation failed because the provision was done too early.
	 */
	TRANSACTION_REJECTED_NAMESPACE_PROVISION_TOO_EARLY(1200 + 128),

	/**
	 * Validation failed because the namespace is reserved.
	 */
	TRANSACTION_REJECTED_NAMESPACE_RESERVED_ROOT(1200 + 129),

	//endregion

	//region mosaic 14x

	/**
	 * Validation failed because the mosaic is unknown.
	 */
	TRANSACTION_REJECTED_MOSAIC_UNKNOWN(1200 + 141),

	/**
	 * Validation failed because the mosaic already exists.
	 */
	TRANSACTION_REJECTED_MOSAIC_ALREADY_EXISTS(1200 + 142),

	/**
	 * Validation failed because the transaction signer is not the creator of the mosaic.
	 */
	TRANSACTION_REJECTED_MOSAIC_CREATOR_CONFLICT(1200 + 143),

	/**
	 * Validation failed because the smart tiles quantity is immutable and there there was already a supply transaction.
	 */
	TRANSACTION_REJECTED_MOSAIC_QUANTITY_IMMUTABLE(1200 + 144),

	/**
	 * Validation failed because the overall smart tiles quantity is exceeded.
	 */
	TRANSACTION_REJECTED_MOSAIC_MAX_QUANTITY_EXCEEDED(1200 + 145),

	/**
	 * Validation failed because the resulting smart tiles quantity for the account would be negative.
	 */
	TRANSACTION_REJECTED_MOSAIC_QUANTITY_NEGATIVE(1200 + 146);

	private final int value;

	NisValidationException(final int value) {
		this.value = value;
	}

	@Override
	public int value() {
		return this.value;
	}
}
