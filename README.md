# XML Manipulation Library
This project is a Kotlin-based library designed to manipulate XML files. 
The library allows for adding, removing, renaming, and transforming XML entities and attributes. Additionally, it supports annotations for XML entity conversion and transformation.

## Features
- Add XML Entities: Add new entities to an XML document.
- Remove XML Entities: Remove specific entities from an XML document.
- Rename XML Entities: Rename entities within an XML document.
- Add Attributes to Entities: Add attributes to XML entities.
- Rename XML Attributes: Rename attributes of entities within an XML document.
- Remove XML Attributes: Remove specific attributes from entities within an XML document.
- XPath Queries: Execute XPath queries to extract parts of the XML.
- Annotations: Use annotations to transform entities and attributes during XML generation.

## How to use

### Creating an XML Document
`val xmlDocument = XMLDocument("root")`

### Creating an XML File
`xmlDocument.createXMLFile("Xml", "output.xml")`

### Adding Entities/Attributes
```
val entity = XMLEntity("fuc", "")
entity.addAttribute("codigo", "M4310")
entity.addChild(XMLEntity("nome", "Programação Avançada"))
entity.addChild(XMLEntity("ects", "6.0"))
xmlDocument.addEntity(entity)
```

### Removing Entities
`xmlDocument.removeEntities("nome")`

### Renaming Entities
`xmlDocument.renameEntities("fuc", "disciplina")`

### Renaming child Entities
`entity.renameChild("nome", "name") `

### Renaming Attributes
`xmlDocument.renameAttributes("fuc", "codigo", "id")`

### Removing Attributes
`xmlDocument.removeAttributes("fuc", "codigo")`

### XPath Query
```
val result = xmlDocument.checkXPath("fuc/avaliacao/componente")
println(result)
```

## Annotations

### Adding a percentage sign to Attribute values
```
class AddPercentage : StringTransformer {
    override fun transform(value: String): String = "$value%"
}

@XmlElement("componente")
data class ComponenteAvaliacao(
    @XmlAttribute val nome: String,
    @XmlAttribute @XmlString(AddPercentage::class) val peso: Int
)

val c1 = ComponenteAvaliacao("Quizzes", 20)
val xmlEntity = createXMLEntityFromClass(c1)
```

### Excluding fields from XML
```
@XmlElement("fuc")
data class FUC(
    @XmlAttribute val codigo: String,
    @XmlElement val nome: String,
    @XmlElement val ects: Double,
    @XmlExclude val observacoes: String
)
val f = FUC("M4310", "Programação Avançada", 6.0, "xxxx")
val xmlEntity = createXMLEntityFromClass(f)
```

### Adapting Entities
```
class FUCAdapter : XMLAdapterinter {
    override fun adapt(entity: XMLEntity): XMLEntity {
        entity.removeChildByName("ects")
        return entity
    }
}

@XmlAdapter(FUCAdapter::class)
@XmlElement("fuc")
data class FUC(
    @XmlAttribute val codigo: String,
    @XmlElement val nome: String,
    @XmlElement val ects: Double,
    @XmlExclude val observacoes: String
)
val f = FUC("M4310", "Programação Avançada", 6.0, "xxxx")
val xmlEntity = createXMLEntityFromClass(f)
```
