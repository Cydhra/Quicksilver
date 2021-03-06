package net.cydhra.quicksilver.library

import kotlinx.serialization.json.Json
import net.cydhra.quicksilver.data.pack.GamePackDefinition
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.File
import java.io.OutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream

const val GAME_PACK_MAGIC_BYTES = 0x47414d45

/**
 * A serializer for a game pack file.
 * @param definition the [GamePackDefinition] instance that will be serialized into the pack file
 * @param paths an array of files and folders to be zipped into the pack file.
 */
class GamePackSerializer(
    private val definition: GamePackDefinition,
    private val paths: Array<File>
) {

    /**
     * Create the pack file into the given output stream.
     *
     * @param out an output stream where to write the pack file data into
     */
    fun pack(out: OutputStream) {
        val dataOut = DataOutputStream(out)

        dataOut.writeInt(GAME_PACK_MAGIC_BYTES)
        dataOut.writeUTF(Json.encodeToString(GamePackDefinition.serializer(), this.definition))

        val zipOutputStream = ZipOutputStream(out).apply { setLevel(9) }

        this.paths.forEach { path ->
            fun putFile(file: File) {
                if (!file.isDirectory) {
                    var entryName = path.name
                    val relativePath = file.toRelativeString(path)
                    if (relativePath != "") {
                        entryName += "/$relativePath"
                    }

                    zipOutputStream.putNextEntry(ZipEntry(entryName))
                    file.inputStream().use { it.copyTo(zipOutputStream) }
                    zipOutputStream.closeEntry()
                } else {
                    var entryName = path.name + '/'
                    val relativePath = file.toRelativeString(path)
                    if (relativePath != "") {
                        entryName += "$relativePath/"
                    }

                    zipOutputStream.putNextEntry(ZipEntry(entryName))
                    zipOutputStream.closeEntry()

                    file.listFiles()?.forEach { putFile(it) }
                }
            }

            putFile(path)
        }
    }
}

/**
 * A deserializer (and installer) for game pack files. It will read the pack file and follow installation instructions.
 *
 * @param libraryPath the base path of the game library where to put all games.
 * @param gamePackFile the pack file that shall be installed into the library
 */
class GamePackDeserializer(
    private val libraryPath: File,
    private val gamePackFile: File
) {

    /**
     * Unpack the game pack file into the library
     */
    fun unpack(): GamePackDefinition {
        assert(libraryPath.exists())
        assert(libraryPath.isDirectory)
        assert(gamePackFile.exists())
        assert(gamePackFile.isFile)

        val inputStream = gamePackFile.inputStream()
        val dataInputStream = DataInputStream(inputStream)

        // check magic bytes
        val magic = dataInputStream.readInt()
        if (magic != GAME_PACK_MAGIC_BYTES)
            throw IllegalArgumentException("provided file is not a valid game pack file")

        // read the game definition and parse it
        val definitionJson = dataInputStream.readUTF()
        val definition = Json.decodeFromString(GamePackDefinition.serializer(), definitionJson)

        // create folder for game and write game definition file into it
        val destinationDirectory = File(libraryPath, definition.info.id)
        destinationDirectory.mkdir()
        File(destinationDirectory, definition.info.id + ".json").apply { createNewFile() }.writeText(definitionJson)

        val zipInputStream = ZipInputStream(inputStream)
        var currentEntry = zipInputStream.nextEntry
        while (currentEntry != null) {
            val outputFile = File(destinationDirectory, currentEntry.name)
            if (currentEntry.isDirectory) {
                outputFile.mkdir()
            } else {
                outputFile.createNewFile()
                outputFile.writeBytes(zipInputStream.readBytes())
            }

            zipInputStream.closeEntry()
            currentEntry = zipInputStream.nextEntry
        }

        zipInputStream.close()
        return definition
    }
}