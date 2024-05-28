package com.diogovaz.xml.api

import java.io.File
import kotlin.reflect.KClass
import kotlin.reflect.KProperty
import kotlin.reflect.full.*


/**
 * Represents an XML entity with a name, optional text content, children and attributes.
 *
 * @property name The name of the XML entity.
 * @property texto The text of the XML entity. If non-empty, the entity cannot have children.
 * @constructor Creates an XML entity with the specified name and text.
 */
data class XMLEntity(var name: String, val texto: String ) {

    val children: MutableList<XMLEntity> = mutableListOf()
    val attributes: MutableMap<String, String> = mutableMapOf()

    /**
     * Adds a child entity to this entity.
     *
     * @param child The child entity to be added. It must have a name consisting only of letters.
     * @throws IllegalArgumentException If the child's name contains non-letter characters.
     * @throws IllegalArgumentException If this entity contains text, as it cannot have both text and child entities.
     */
    fun addChild(child: XMLEntity) {
        require(child.name.matches(Regex("[a-zA-Z]+"))) { "The name of the child must contain only letters." }
        require(texto==""){"Cannot add a child because the entity already contains text."}
        children.add(child)
    }

    /**
     * Removes a child from this entity.
     *
     * @param child The child entity to be removed.
     */
    fun removeChild(child: XMLEntity) {
        children.remove(child)

    }

    /**
     * Removes child entities with the specified name from this entity.
     *
     * @param st The name of the child entities to be removed.
     */
    fun removeChildByName(st: String){
        val listaElementoEleminar = mutableListOf<XMLEntity>()
        for (child in children){
            if(child.name == st){
                listaElementoEleminar.add(child)

            }
        }
        listaElementoEleminar.forEach{removeChild(it)}
    }

    /**
     * Adds an attribute to this entity.
     *
     * @param name The name of the attribute to be added.
     * @param value The value of the attribute to be added.
     */
    fun addAttribute(name: String, value: String) {
        attributes[name] = value

    }

    /**
     * Removes the attribute with the specified name.
     *
     * @param name The name of the attribute to remove.
     */
    fun removeAttribute(name: String){
        attributes.remove(name)

    }
    /**
     * Renames entities with the specified old name to the new name.
     *
     * @param oldName The current name of the chield to be renamed.
     * @param newName The new name to assign to the chield.
     */
    fun renameChild(oldName: String, newName: String) {
        accept {
            if (it.name == oldName) {
                it.name = newName
            }
            true
        }
    }
    /**
     * Renames an attribute of this entity.
     *
     * @param oldAttributeName The current name of the attribute to be renamed.
     * @param newAttributeName The new name to assign to the attribute.
     */
    fun renameAttribute(oldAttributeName: String, newAttributeName: String) {
        val index = attributes.keys.indexOf(oldAttributeName)
        if (index != -1) {
            val value = attributes.remove(oldAttributeName)
            if (value != null) {
                attributes[newAttributeName] = value
            }
        }
    }

    /**
     * Accepts a visitor function to traverse this entity and its children.
     *
     * @param visitor The visitor function to be applied to this entity and its children. The function takes an XMLEntity as a parameter and returns a Boolean indicating whether to continue traversing the children.
     * @receiver
     */
    fun accept(visitor: (XMLEntity) -> Boolean) {
        if (visitor(this)) {
            children.forEach { it.accept(visitor) }
        }
    }

}

/**
 * Represents an XML document with a root entity.
 *
 * @property nomeRoot The name of the root element of the XML document.
 * @constructor Creates an XML document with the specified root name.
 */
data class XMLDocument(var nomeRoot: String){
    private val rootEntity: XMLEntity = XMLEntity(nomeRoot, "")

    /**
     * Accepts a visitor function to traverse the root entity and its children.
     *
     * @param visitor The visitor function to be applied to the root entity and its children. The function takes an XMLEntity as a parameter and returns a Boolean indicating whether to continue traversing the children.
     *
     * @receiver
     */
    fun accept(visitor: (XMLEntity) -> Boolean){
        rootEntity.accept(visitor)
    }

    /**
     * Checks if a string contains only letters.
     *
     * @receiver The string to check.
     * @return true if the string contains only letters, false otherwise.
     */
    fun String.onlyLetters() = all { it.isLetter() }

    /**
     * Adds an entity to the root entity.
     *
     * @param entity The entity to be added. The name of the entity must consist only of letters.
     * @throws IllegalArgumentException If the entity's name contains non-letter characters.
     */
    fun addEntity(entity: XMLEntity) {
        require(entity.name.matches(Regex("[a-zA-Z]+"))) { "The name of the entity must contain only letters." }
        rootEntity.addChild(entity)
    }

    /**
     * Removes the specified entity from the root entity.
     *
     * @param entity The entity to be removed.
     */
    fun removeEntity(entity: XMLEntity) {
        rootEntity.removeChild(entity)
    }

    /**
     * Generates a pretty-printed XML representation of the document.
     *
     * @return A formatted string representing the XML document.
     */
    fun prettyPrint(): String {
        return StringBuilder().apply {
            append("<?xml version = \"1.0\" encoding = \"UTF-8\"?>\n")
            append(prettyPrintEntity(rootEntity, 0))
        }.toString()
    }

    private fun prettyPrintEntity(entity: XMLEntity, depth: Int): String {
        val indent = " ".repeat(depth * 4)
        val builder = StringBuilder()
        builder.append("$indent<${entity.name}")
        entity.attributes.forEach { (name, value) ->
            builder.append(" $name=\"$value\"")
        }

        if (entity.children.isEmpty()) {
            if (entity.attributes.isEmpty()) {
                if (entity.texto == "") {
                    builder.append("/>")
                } else {
                    builder.append(">${entity.texto}</${entity.name}>")
                }
            } else {
                builder.append("/>")
            }
        } else {
            builder.append(">\n")
            entity.children.forEach { child ->
                builder.append(prettyPrintEntity(child, depth + 1))
            }
            builder.append("$indent</${entity.name}>")
        }
        builder.append("\n")
        return builder.toString()
    }


    /**
     * Renames entities with the specified old name to the new name.
     *
     * @param oldName The current name of the entities to be renamed.
     * @param newName The new name to assign to the entities.
     */
    fun renameEntities(oldName: String, newName: String) {
        rootEntity.accept { if(it.name == oldName) it.name = newName; true }
    }

    /**
     * Renames attributes of a specific entity.
     *
     * @param entityName The name of the entity whose attributes are to be renamed.
     * @param oldAttributeName The current name of the attribute to be renamed.
     * @param newAttributeName The new name to assign to the attribute.
     */
    fun renameAttributes(entityName: String, oldAttributeName: String, newAttributeName: String) {
        rootEntity.accept {
            if (it.name == entityName) {
                it.renameAttribute(oldAttributeName, newAttributeName)
            }
            true
        }
    }

    /**
     * Removes entities with the specified name from the root entity.
     *
     * @param entityName The name of the entities to be removed.
     */
    fun removeEntities(entityName: String) {
        rootEntity.accept { it ->
            it.children.removeAll { it.name == entityName }
            true
        }
    }

    /**
     * Removes attributes from a specific entity.
     *
     * @param entityName The name of the entity whose attribute is to be removed.
     * @param attributeName The name of the attribute to be removed.
     */
    fun removeAttributes(entityName: String, attributeName: String) {
        rootEntity.accept { if(it.name == entityName ) it.removeAttribute(attributeName); true }
    }

    /**
     * Evaluates an XPath expression against the XML document and returns the matching entities.
     *
     * @param xpathExpression The XPath expression to evaluate.
     * @return A string representing the XML structure that matches the XPath expression.
     */
    fun checkXPath(xpathExpression: String): String {
        val entityNames = xpathExpression.split("/")
        return checkXPathHelper(rootEntity, entityNames)
    }

    private fun checkXPathHelper(entity: XMLEntity, entityNames: List<String>): String {
        if (entityNames.isEmpty()) {
            return if (entity.attributes.isNotEmpty()) {
                "<${entity.name} ${entity.attributes.entries.joinToString(" ") { "${it.key}=\"${it.value}\"" }}/>\n"
            } else {
                "<${entity.name}/>\n"
            }
        }

        val currentEntityName = entityNames.first()
        val remainingEntityNames = entityNames.drop(1)

        val matchingEntities = mutableListOf<String>()

        entity.accept { if (it.name == currentEntityName)
            matchingEntities.add(checkXPathHelper(it, remainingEntityNames)); true}

        return matchingEntities.joinToString("")
    }

    /**
     * Creates an XML file in the specified directory with the given file name.
     *
     * @param directory The directory where the XML file will be created.
     * @param fileName The name of the XML file to be created.
     * @throws IllegalArgumentException If the directory is invalid or cannot be created.
     */
    fun createXMLFile(directory: String, fileName: String) {
        val dir = File(directory)
        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                throw IllegalArgumentException("The directory $directory could not be created.")
            }
        } else if (!dir.isDirectory) {
            throw IllegalArgumentException("The path $directory is not a directory.")
        }

        val file = File(dir, fileName)
        val xmlContent = prettyPrint()
        file.writeText(xmlContent)
    }

    /**
     * Normalizes the content of an XML file by removing extra whitespaces.
     *
     * @param xml The XML content to normalize.
     * @return A normalized string with extra whitespace removed.
     */
    fun normalizeXMLFile(xml: String): String {
        return xml.replace("\\s+".toRegex(), " ").trim()
    }
}

/**
 * Annotation to specify the XML element name for properties or classes in XML serialization.
 *
 * @property name The name of the XML element.
 * @constructor Creates an XmlElement annotation with the specified element name.
 */
@Target(AnnotationTarget.PROPERTY, AnnotationTarget.CLASS)
annotation class XmlElement(val name: String = "")

/**
 * Annotation to specify the XML attribute name for properties in XML serialization.
 *
 * @property name The name of the XML attribute.
 * @constructor Creates an XmlAttribute annotation with the specified attribute name.
 */
@Target(AnnotationTarget.PROPERTY)
annotation class XmlAttribute(val name: String ="")

/**
 * Annotation to exclude a property from XML serialization.
 *
 * @constructor Creates an XmlExclude annotation.
 */
@Target(AnnotationTarget.PROPERTY)
annotation class XmlExclude

/**
 * Xml string Annotation to specify a custom string transformation for XML serialization.
 *
 * @property transformer The class implementing the StringTransformer interface for custom string transformation.
 * @constructor Creates an XmlString annotation with the specified transformer.
 */
@Target(AnnotationTarget.PROPERTY)
annotation class XmlString(val transformer: KClass<out StringTransformer>)

/**
 * Annotation to specify a custom XML adapter for XML serialization.
 *
 * @property adapter The class implementing the XMLAdapter interface for custom XML serialization.
 * @constructor Creates an XmlAdapter annotation with the specified adapter.
 */
@Target(AnnotationTarget.CLASS)
annotation class XmlAdapter(val adapter: KClass<out XMLAdapterinter>)

/**
 * Interface for string transformation in XML serialization.
 *
 */
interface StringTransformer {
    /**
     * Transforms a string value according to specific rules or requirements.
     *
     * @param value The input string to be transformed.
     * @return The transformed string value.
     */
    fun transform(value: String): String
}

/**
 * Interface for adapting XML entities after mapping.
 *
 */
interface XMLAdapterinter {
    /**
     * Adapts an XML entity according to specific rules or requirements.
     *
     * @param entity The input XML entity to be adapted.
     * @return The adapted XML entity.
     */
    fun adapt(entity: XMLEntity) : XMLEntity
}


/**
 * Creates an XML entity from a given class instance.
 *
 * @param instance The instance of the class from which the XML entity is to be created.
 * @return The generated XMLEntity object.
 * @throws IllegalArgumentException If the class or its properties are not properly annotated or if there's an error during entity creation.
 */
fun createXMLEntityFromClass(instance: Any): XMLEntity {
    val clazz = instance::class

    if(clazz.annotations.find{it is XmlElement} == null){
        throw  IllegalArgumentException("The provided class does not have the required XmlElement annotation.")
    }
    val x = clazz.annotations.find {it is XmlElement} as XmlElement

    val entityName = x.name.ifBlank {
        clazz.simpleName!!.lowercase()
    }

    var xmlEntity = XMLEntity(entityName, "")


    for (prop in clazz.classFields) {
        if (prop.findAnnotation<XmlExclude>() != null) continue

        when{
            prop.hasAnnotation<XmlExclude>()->{}

            prop.hasAnnotation<XmlElement>()-> {
                val name = prop.findAnnotation<XmlElement>()?.name?.ifBlank {
                    prop.name.lowercase()
                }
                val propValue = prop.call(instance)
                if(propValue is List<*>){
                    val entityProp = XMLEntity(prop.name,"")

                    propValue.filterNotNull().forEach {
                        val childEntity = createXMLEntityFromClass(it)

                        entityProp.addChild(childEntity)

                    }
                    xmlEntity.addChild(entityProp)
                }else{
                    val entity = XMLEntity(name.toString(), propValue.toString())
                    xmlEntity.addChild(entity)
                }


            }
            prop.hasAnnotation<XmlAttribute>()->{
                val name = prop.findAnnotation<XmlAttribute>()?.name?.ifBlank {
                    prop.name.lowercase()
                }
                val propValue = prop.call(instance)
                var value = propValue.toString()

                prop.findAnnotation<XmlString>()?.let {
                    val transformer = it.transformer.objectInstance ?: it.transformer.createInstance()
                    value = transformer.transform(value)
                }
                xmlEntity.addAttribute(name.toString(),value)
            }

        }
    }

    val adapt = clazz.findAnnotation<XmlAdapter>()
    if(adapt != null){
        xmlEntity = adapt.adapter.createInstance().adapt(xmlEntity)
    }
    return xmlEntity
}

val KClass<*>.classFields: List<KProperty<*>>
    get() {
        return primaryConstructor!!.parameters.map { p ->
            declaredMemberProperties.find { it.name == p.name }!!
        }
    }