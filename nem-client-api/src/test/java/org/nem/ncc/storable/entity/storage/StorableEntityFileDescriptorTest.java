package org.nem.ncc.storable.entity.storage;

import net.minidev.json.JSONObject;
import org.apache.commons.io.FileUtils;
import org.hamcrest.core.*;
import org.junit.*;
import org.mockito.Mockito;
import org.nem.core.serialization.JsonSerializer;
import org.nem.ncc.storable.entity.*;
import org.nem.ncc.test.ExceptionAssert;

import java.io.*;
import java.nio.file.Paths;

public abstract class StorableEntityFileDescriptorTest<
		TEntityName extends StorableEntityName,
		TEntityFileExtension extends StorableEntityFileExtension,
		TEntityFileDescriptor extends StorableEntityFileDescriptor<?, TEntityName, TEntityFileExtension>> {
	protected static final String WORKING_DIRECTORY = System.getProperty("user.dir");
	protected static final File TEST_FILE_DIRECTORY = new File(WORKING_DIRECTORY, "test_files");
	protected static final File TEST_FILE = new File(TEST_FILE_DIRECTORY, "test.bar");
	protected static final File TEST_FILE_BAD_EXT = new File(TEST_FILE_DIRECTORY, "test.foobar");

	//region BeforeClass / AfterClass

	@BeforeClass
	public static void createTestFiles() throws IOException {
		final boolean result = TEST_FILE_DIRECTORY.mkdir()
				&& TEST_FILE.createNewFile()
				&& TEST_FILE_BAD_EXT.createNewFile();

		if (!result) {
			throw new RuntimeException("unable to initialize test suite");
		}
	}

	@AfterClass
	public static void removeTestFiles() throws IOException {
		FileUtils.deleteDirectory(TEST_FILE_DIRECTORY);
	}

	//endregion

	//region construction

	@Test
	public void descriptorCannotBeCreatedAroundDirectory() {
		// Assert:
		ExceptionAssert.assertThrowsStorageException(
				v -> this.createDescriptor(TEST_FILE_DIRECTORY),
				this.getExceptionClass(),
				this.getExceptionValue(StorableEntityStorageException.Code.STORABLE_ENTITY_CANNOT_BE_DIRECTORY.value()));
	}

	@Test
	public void descriptorCannotBeCreatedAroundStorableEntityWithInvalidExtension() {
		// Assert:
		ExceptionAssert.assertThrowsStorageException(
				v -> this.createDescriptor(TEST_FILE_BAD_EXT),
				this.getExceptionClass(),
				this.getExceptionValue(StorableEntityStorageException.Code.STORABLE_ENTITY_HAS_INVALID_EXTENSION.value()));
	}

	@Test
	public void descriptorCanBeCreatedAroundStorableEntityWithValidExtension() {
		// Arrange:
		final File file = new File(Paths.get(TEST_FILE_DIRECTORY.toString(), "blah").toString(), "foo.bar");

		// Act:
		final TEntityFileDescriptor descriptor = this.createDescriptor(file);

		// Assert:
		Assert.assertThat(descriptor.getName(), IsEqual.equalTo(this.createEntityName("foo")));
		Assert.assertThat(descriptor.getFileExtension(), IsEqual.equalTo(this.createEntityFileExtension(".bar")));
		Assert.assertThat(
				descriptor.getStorableEntityLocation(),
				IsEqual.equalTo(Paths.get(TEST_FILE_DIRECTORY.toString(), "blah", "foo.bar").toString()));
	}

	@Test
	public void descriptorCanBeCreatedAroundStorableEntityWithMixedCaseExtension() {
		// Arrange:
		final File file = new File(Paths.get(TEST_FILE_DIRECTORY.toString(), "BlAh").toString(), "FoO.BaR");

		// Act:
		final TEntityFileDescriptor descriptor = this.createDescriptor(file);

		// Assert:
		Assert.assertThat(descriptor.getName(), IsEqual.equalTo(this.createEntityName("FoO")));
		Assert.assertThat(descriptor.getFileExtension(), IsEqual.equalTo(this.createEntityFileExtension(".BaR")));
		Assert.assertThat(
				descriptor.getStorableEntityLocation(),
				IsEqual.equalTo(Paths.get(TEST_FILE_DIRECTORY.toString(), "BlAh", "FoO.BaR").toString()));
	}

	@Test
	public void descriptorCanBeCreatedAroundUrlEncodedStorableEntityWithValidExtension() {
		// Arrange:
		final File file = new File(Paths.get(TEST_FILE_DIRECTORY.toString(), "blah").toString(), "%C3%B6%C3%A4%C3%BC%40.bar");

		// Act:
		final TEntityFileDescriptor descriptor = this.createDescriptor(file);

		// Assert:
		Assert.assertThat(descriptor.getName(), IsEqual.equalTo(this.createEntityName("öäü@")));
		Assert.assertThat(descriptor.getFileExtension(), IsEqual.equalTo(this.createEntityFileExtension(".bar")));
		Assert.assertThat(
				descriptor.getStorableEntityLocation(),
				IsEqual.equalTo(Paths.get(TEST_FILE_DIRECTORY.toString(), "blah", "%C3%B6%C3%A4%C3%BC%40.bar").toString()));
	}

	//endregion

	//region openRead

	@Test
	public void openReadCanOpenFileThatExists() throws IOException {
		// Arrange:
		final TEntityFileDescriptor descriptor = this.createDescriptor(TEST_FILE);

		// Act:
		try (final InputStream is = descriptor.openRead()) {
			// Assert:
			Assert.assertThat(is, IsNull.notNullValue());
		}
	}

	@Test
	public void openReadCannotOpenFileThatDoesNotExist() {
		// Arrange:
		final File file = new File(TEST_FILE_DIRECTORY, "imaginary-read.bar");
		final TEntityFileDescriptor descriptor = this.createDescriptor(file);

		// Act:
		ExceptionAssert.assertThrowsStorageException(
				v -> descriptor.openRead(),
				this.getExceptionClass(),
				this.getExceptionValue(StorableEntityStorageException.Code.STORABLE_ENTITY_DOES_NOT_EXIST.value()));
	}

	@Test
	public void openReadCannotOpenFileThatIsInvalid() {
		// Arrange:
		final File file = Mockito.mock(File.class);
		Mockito.when(file.getName()).thenReturn("fo\0o.bar");
		Mockito.when(file.getAbsolutePath()).thenReturn("fo\0o.bar");
		Mockito.when(file.exists()).thenReturn(true);
		final TEntityFileDescriptor descriptor = this.createDescriptor(file);

		// Act:
		ExceptionAssert.assertThrowsStorageException(
				v -> descriptor.openRead(),
				this.getExceptionClass(),
				this.getExceptionValue(StorableEntityStorageException.Code.STORABLE_ENTITY_COULD_NOT_BE_READ.value()));
	}

	@Test
	public void openReadThrowsIfModeIsNotRaw() {
		// Arrange:
		final TEntityFileDescriptor descriptor = this.createDescriptor(TEST_FILE);

		// Act + Assert:
		ExceptionAssert.assertThrows(v -> descriptor.openRead(StorableEntityReadMode.Decode), IllegalArgumentException.class);
	}

	@Test
	public void openReadWithReadModeDelegatesToOpenRead() throws IOException {
		// Arrange:
		final TEntityFileDescriptor descriptor = Mockito.spy(this.createDescriptor(TEST_FILE));

		// Act + Assert:
		try (final InputStream is = descriptor.openRead(StorableEntityReadMode.Raw)) {
			Mockito.verify(descriptor, Mockito.times(1)).openRead();
		}
	}

	//endregion

	//region openWrite

	@Test
	public void openWriteCanOpenFileThatExists() throws IOException {
		// Arrange:
		final TEntityFileDescriptor descriptor = this.createDescriptor(TEST_FILE);

		// Act:
		try (final OutputStream os = descriptor.openWrite()) {
			// Assert:
			Assert.assertThat(os, IsNull.notNullValue());
		}
	}

	@Test
	public void openWriteCanOpenFileThatDoesNotExist() throws IOException {
		// Arrange:
		final File file = new File(TEST_FILE_DIRECTORY, "imaginary-write.bar");
		final TEntityFileDescriptor descriptor = this.createDescriptor(file);

		// Act:
		try (final OutputStream os = descriptor.openWrite()) {
			// Assert:
			Assert.assertThat(os, IsNull.notNullValue());
		}
	}

	@Test
	public void openWriteCannotOpenFileThatIsInvalid() {
		// Arrange:
		final File file = Mockito.mock(File.class);
		Mockito.when(file.getName()).thenReturn("fo\0o.bar");
		Mockito.when(file.getAbsolutePath()).thenReturn("fo\0o.bar");
		final TEntityFileDescriptor descriptor = this.createDescriptor(file);

		// Act:
		ExceptionAssert.assertThrowsStorageException(
				v -> descriptor.openWrite(),
				this.getExceptionClass(),
				this.getExceptionValue(StorableEntityStorageException.Code.STORABLE_ENTITY_COULD_NOT_BE_SAVED.value()));
	}

	//endregion

	//region delete

	@Test
	public void deleteDeletesUnderlyingStorableEntityFile() throws IOException {
		// Arrange:
		final File file = new File(TEST_FILE_DIRECTORY, "to-be-deleted.bar");
		final TEntityFileDescriptor descriptor = this.createDescriptor(file);
		Assert.assertThat(file.createNewFile(), IsEqual.equalTo(true));

		// Act:
		descriptor.delete();

		// Assert:
		Assert.assertThat(file.exists(), IsEqual.equalTo(false));
	}

	@Test
	public void deleteRaisesExceptionIfFileCannotBeDeleted() {
		// Arrange:
		final File file = Mockito.mock(File.class);
		Mockito.when(file.getName()).thenReturn("foo.bar");
		Mockito.when(file.getAbsolutePath()).thenReturn("foo.bar");
		final TEntityFileDescriptor descriptor = this.createDescriptor(file);

		Mockito.when(file.delete()).thenReturn(false);

		// Act:
		ExceptionAssert.assertThrowsStorageException(
				v -> descriptor.delete(),
				this.getExceptionClass(),
				this.getExceptionValue(StorableEntityStorageException.Code.STORABLE_ENTITY_COULD_NOT_BE_DELETED.value()));
	}

	//endregion

	//region serialization

	@Test
	public void descriptorCanBeSerialized() {
		// Arrange:
		final File file = new File(Paths.get(TEST_FILE_DIRECTORY.toString(), "blah").toString(), "foo.bar");
		final TEntityFileDescriptor descriptor = this.createDescriptor(file);

		// Act:
		final JSONObject jsonObject = JsonSerializer.serializeToJson(descriptor);

		// Assert:
		Assert.assertThat(jsonObject.size(), IsEqual.equalTo(2));
		Assert.assertThat(jsonObject.get(descriptor.getName().getLabel()), IsEqual.equalTo("foo"));
		Assert.assertThat(
				jsonObject.get("location"),
				IsEqual.equalTo(Paths.get(TEST_FILE_DIRECTORY.toString(), "blah", "foo.bar").toString()));
	}

	//endregion

	@Test
	public void getStorableEntityLocationReturnsAbsolutePathToStorableEntity() {
		// Arrange:
		final File file = new File(Paths.get(TEST_FILE_DIRECTORY.toString(), "blah").toString(), "foo.bar");
		final TEntityFileDescriptor descriptor = this.createDescriptor(file);

		// Act:
		final String path = descriptor.getStorableEntityLocation();

		// Assert:
		Assert.assertThat(path, IsEqual.equalTo(file.getAbsolutePath()));
	}

	protected abstract TEntityFileDescriptor createDescriptor(final File file);

	protected abstract Class<? extends StorableEntityStorageException> getExceptionClass();

	protected abstract Integer getExceptionValue(final Integer originalValue);

	private TEntityName createEntityName(final String name) {
		return this.createDescriptor(new File(name + ".xyz")).getName();
	}

	private TEntityFileExtension createEntityFileExtension(final String extension) {
		return this.createDescriptor(new File("xyz." + extension)).getFileExtension();
	}
}
