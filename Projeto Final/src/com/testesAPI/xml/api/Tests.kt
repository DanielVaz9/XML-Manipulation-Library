package com.testesAPI.xml.api

import com.diogovaz.xml.api.*
import org.testng.Assert.assertEquals
import org.testng.annotations.Test
import org.junit.jupiter.api.assertThrows
import java.io.File

class Tests {

    @Test
    fun testAddEntity() {
        val xmlDocument = XMLDocument("root")

        val entity = XMLEntity("fuc","")
        entity.addAttribute("codigo", "M4310")
        entity.addChild(XMLEntity("nome","Programação Avançada"))
        entity.addChild(XMLEntity("ects","6.0"))

        xmlDocument.addEntity(entity)

        val outputFile = "testAddEntity_output.xml"
        xmlDocument.createXMLFile("TestResults",outputFile)

        val outputXmlContent = xmlDocument.normalizeXMLFile(File("TestResults/$outputFile").readText())

        val expectedXml = xmlDocument.normalizeXMLFile("""
        <?xml version = "1.0" encoding = "UTF-8"?>
        <root>
            <fuc codigo="M4310">
                <nome>Programação Avançada</nome>
                <ects>6.0</ects>
            </fuc>
        </root>
    """.trimIndent())

        assert(outputXmlContent == expectedXml) { "XML files are not identical" }
    }


    @Test
    fun testRemoveEntity() {
        val xmlDocument = XMLDocument("root")

        val entityToKeep = XMLEntity("fuc","")
        entityToKeep.addAttribute("codigo", "03782")
        entityToKeep.addChild(XMLEntity("nome","Dissertação"))
        entityToKeep.addChild(XMLEntity("ects","42.0"))

        xmlDocument.addEntity(entityToKeep)

        xmlDocument.removeEntities("nome")

        val outputFile = "testRemoveEntity_output.xml"
        xmlDocument.createXMLFile("TestResults",outputFile)
        val outputXmlContent = xmlDocument.normalizeXMLFile(File("TestResults/$outputFile").readText())

        val expectedXml = xmlDocument.normalizeXMLFile("""
            <?xml version = "1.0" encoding = "UTF-8"?>
            <root>
                <fuc codigo="03782">
                    <ects>42.0</ects>
                </fuc>
            </root>
        """.trimIndent())

        assert(outputXmlContent == expectedXml) { "XML files are not identical" }
    }

    @Test
    fun testRenameEntity() {
        val xmlDocument = XMLDocument("root")
        val entity = XMLEntity("fuc","")
        entity.addAttribute("codigo", "M4310")
        entity.addChild(XMLEntity("nome","Programação Avançada"))
        val x = XMLEntity("ects","6.0")
        entity.addChild(x)
        entity.renameChild(x.name,x.name.reversed())

        xmlDocument.addEntity(entity)

        xmlDocument.renameEntities("fuc", "disciplina")

        val outputFile = "testRenameEntity_output.xml"
        xmlDocument.createXMLFile("TestResults",outputFile)
        val outputXmlContent = xmlDocument.normalizeXMLFile(File("TestResults/$outputFile").readText())

        val expectedXml = xmlDocument.normalizeXMLFile("""
            <?xml version = "1.0" encoding = "UTF-8"?>
            <root>
                <disciplina codigo="M4310">
                    <nome>Programação Avançada</nome>
                    <stce>6.0</stce>
                </disciplina>
            </root>
        """.trimIndent())

        assert(outputXmlContent == expectedXml) { "XML files are not identical" }
    }

    @Test
    fun testRenameAttribute() {
        val xmlDocument = XMLDocument("root")
        val entity = XMLEntity("fuc","")
        entity.addAttribute("codigo", "M4310")
        entity.addChild(XMLEntity("nome","Programação Avançada"))
        entity.addChild(XMLEntity("ects","6.0"))
        val entidade2 = XMLEntity("avaliacao","")
        val entidade3 = (XMLEntity("componente",""))
        val entidade4 = (XMLEntity("componente",""))
        entidade3.addAttribute("nome","Projeto")
        entidade3.addAttribute("peso","80%")

        entidade4.addAttribute("nome","Quizzs")
        entidade4.addAttribute("peso","20%")
        entidade2.addChild(entidade3)
        entidade2.addChild(entidade4)
        entity.addChild(entidade2)

        xmlDocument.addEntity(entity)

        xmlDocument.renameAttributes("fuc", "codigo", "id")
        xmlDocument.renameAttributes("componente", "nome", "name")

        val outputFile = "testRenameAttribute_output.xml"
        xmlDocument.createXMLFile("TestResults",outputFile)
        val outputXmlContent = xmlDocument.normalizeXMLFile(File("TestResults/$outputFile").readText())

        val expectedXml = xmlDocument.normalizeXMLFile("""
            <?xml version = "1.0" encoding = "UTF-8"?>
            <root>
                <fuc id="M4310">
                    <nome>Programação Avançada</nome>
                    <ects>6.0</ects>
                    <avaliacao>
                        <componente peso="80%" name="Projeto"/>
                        <componente peso="20%" name="Quizzs"/>
                    </avaliacao>
                </fuc>
            </root>
        """.trimIndent())

        assert(outputXmlContent == expectedXml) { "XML files are not identical" }
    }

    @Test
    fun testRemoveAttribute() {
        val xmlDocument = XMLDocument("root")
        val entity = XMLEntity("fuc","")
        entity.addAttribute("codigo", "M4310")
        entity.addChild(XMLEntity("nome","Programação Avançada"))
        entity.addChild(XMLEntity("ects","6.0"))



        xmlDocument.addEntity(entity)

        xmlDocument.removeAttributes("fuc","codigo")

        val outputFile = "testRemoveAttribute_output.xml"
        xmlDocument.createXMLFile("TestResults",outputFile)
        val outputXmlContent = xmlDocument.normalizeXMLFile(File("TestResults/$outputFile").readText())

        val expectedXml = xmlDocument.normalizeXMLFile("""
            <?xml version = "1.0" encoding = "UTF-8"?>
            <root>
                <fuc>
                    <nome>Programação Avançada</nome>
                    <ects>6.0</ects>
                </fuc>
            </root>
        """.trimIndent())

        assert(outputXmlContent == expectedXml) { "XML files are not identical" }
    }


    @Test
    fun testCheckXPath() {
        val xmlDocument = XMLDocument("root")
        val entity1 = XMLEntity("fuc", "")
        entity1.addAttribute("codigo", "M4310")
        entity1.addChild(XMLEntity("nome","Programação Avançada"))
        entity1.addChild(XMLEntity("ects","6.0"))
        entity1.apply {
            addChild(XMLEntity("avaliacao", "").apply {
                addChild(XMLEntity("componente","").apply {
                    addAttribute("nome", "Quizzes")
                    addAttribute("peso", "20%")
                })
                addChild(XMLEntity("componente","").apply {
                    addAttribute("nome", "Projeto")
                    addAttribute("peso", "80%")
                })
            })
        }
        val entity2 = XMLEntity("fuc","")
        entity2.addAttribute("codigo", "03782")
        entity2.addChild(XMLEntity("nome","Dissertação"))
        entity2.addChild(XMLEntity("ects","42.0"))
        entity2.apply {
            addChild(XMLEntity("avaliacao","").apply {
                addChild(XMLEntity("componente","").apply {
                    addAttribute("nome", "Dissertação")
                    addAttribute("peso", "60%")
                })
                addChild(XMLEntity("componente","").apply {
                    addAttribute("nome", "Apresentação")
                    addAttribute("peso", "20%")
                })
                addChild(XMLEntity("componente","").apply {
                    addAttribute("nome", "Discussão")
                    addAttribute("peso", "20%")
                })
            })
        }
        xmlDocument.addEntity(entity1)
        xmlDocument.addEntity(entity2)

        val expectedXml = """
            <componente nome="Quizzes" peso="20%"/>
            <componente nome="Projeto" peso="80%"/>
            <componente nome="Dissertação" peso="60%"/>
            <componente nome="Apresentação" peso="20%"/>
            <componente nome="Discussão" peso="20%"/>
        """.trimIndent()

        assertEquals(expectedXml, xmlDocument.checkXPath("fuc/avaliacao/componente").trim())
    }

    @Test
    fun testCreatXML() {
        val xmlDocument = XMLDocument("root")
        val entity1 = XMLEntity("fuc", "")
        entity1.addAttribute("codigo", "M4310")
        entity1.addChild(XMLEntity("nome","Programação Avançada"))
        entity1.addChild(XMLEntity("ects","6.0"))
        entity1.apply {
            addChild(XMLEntity("avaliacao", "").apply {
                addChild(XMLEntity("componente","").apply {
                    addAttribute("nome", "Quizzes")
                    addAttribute("peso", "20%")
                })
                addChild(XMLEntity("componente","").apply {
                    addAttribute("nome", "Projeto")
                    addAttribute("peso", "80%")
                })
            })
        }
        val entity2 = XMLEntity("fuc","")
        entity2.addAttribute("codigo", "03782")
        entity2.addChild(XMLEntity("nome","Dissertação"))
        entity2.addChild(XMLEntity("ects","42.0"))
        entity2.apply {
            addChild(XMLEntity("avaliacao","").apply {
                addChild(XMLEntity("componente","").apply {
                    addAttribute("nome", "Dissertação")
                    addAttribute("peso", "60%")
                })
                addChild(XMLEntity("componente","").apply {
                    addAttribute("nome", "Apresentação")
                    addAttribute("peso", "20%")
                })
                addChild(XMLEntity("componente","").apply {
                    addAttribute("nome", "Discussão")
                    addAttribute("peso", "20%")
                })
            })
        }
        xmlDocument.addEntity(entity1)
        xmlDocument.addEntity(entity2)
        xmlDocument.createXMLFile("TestResults","testCreatXML_output.xml")
    }
    @Test
    fun testAnnotationAddComponement(){

        val c1 = ComponenteAvaliacao("Quizzes", 20)
        val xmlEntity = createXMLEntityFromClass(c1)
        val outputFile = "testAnnotationAddComponement_output.xml"
        val xmlDocument = XMLDocument("root")
        xmlDocument.addEntity(xmlEntity)
        xmlDocument.createXMLFile("TestResults",outputFile)

        val outputXmlContent = xmlDocument.normalizeXMLFile(File("TestResults/$outputFile").readText())

        val expectedXml = xmlDocument.normalizeXMLFile("""
            <?xml version = "1.0" encoding = "UTF-8"?>
            <root>
                <componente nome="Quizzes" peso="20%"/>
            </root>
        """.trimIndent())

        assert(outputXmlContent == expectedXml) { "XML files are not identical" }
    }

    @Test
    fun testAnnotationExclude(){

        val f = FUC("M4310", "Programação Avançada", 6.0, "xxxx")
        val xmlEntity = createXMLEntityFromClass(f)
        val outputFile = "testAnnotationExclude_output.xml"
        val xmlDocument = XMLDocument("root")
        xmlDocument.addEntity(xmlEntity)
        xmlDocument.createXMLFile("TestResults",outputFile)

        val outputXmlContent = xmlDocument.normalizeXMLFile(File("TestResults/$outputFile").readText())

        val expectedXml = xmlDocument.normalizeXMLFile("""
            <?xml version = "1.0" encoding = "UTF-8"?>
            <root>
                <fuc codigo="M4310">
                    <nome>Programação Avançada</nome>
                </fuc>
            </root>
        """.trimIndent())

        assert(outputXmlContent == expectedXml) { "XML files are not identical" }
    }
    @Test
    fun testAnnotationAdaptar(){
        val f = FUC("M4310", "Programação Avançada", 6.0, "xxxx")
        val xmlEntity = createXMLEntityFromClass(f)
        val outputFile = "testAnnotationAdaptar_output.xml"
        val xmlDocument = XMLDocument("root")
        xmlDocument.addEntity(xmlEntity)
        xmlDocument.createXMLFile("TestResults",outputFile)

        val outputXmlContent = xmlDocument.normalizeXMLFile(File("TestResults/$outputFile").readText())

        val expectedXml = xmlDocument.normalizeXMLFile("""
            <?xml version = "1.0" encoding = "UTF-8"?>
            <root>
                <fuc codigo="M4310">
                    <nome>Programação Avançada</nome>
                </fuc>
            </root>
        """.trimIndent())

        assert(outputXmlContent == expectedXml) { "XML files are not identical" }
    }


    @Test
    fun testAnnotationAdaptarWithComponement(){
        val c1 = ComponenteAvaliacao("Quizzes", 20)
        val c2 = ComponenteAvaliacao("Projeto", 80)

        val f = FucCompoment("M4310", "Programação Avançada", 6.0, "xxxx", listOf(c1, c2))
        val xmlEntity = createXMLEntityFromClass(f)

        val outputFile = "testAnnotationAdaptarWithComponement_output.xml"
        val xmlDocument = XMLDocument("root")

        xmlDocument.addEntity(xmlEntity)
        xmlDocument.createXMLFile("TestResults",outputFile)

        val outputXmlContent = xmlDocument.normalizeXMLFile(File("TestResults/$outputFile").readText())

        val expectedXml = xmlDocument.normalizeXMLFile("""
            <?xml version = "1.0" encoding = "UTF-8"?>
            <root>
                <fuc codigo="M4310">
                    <nome>Programação Avançada</nome>
                    <avaliacao>
                        <componente nome="Quizzes" peso="20%"/>
                        <componente nome="Projeto" peso="80%"/>
                    </avaliacao>
                </fuc>
            </root>
        """.trimIndent())
        assert(outputXmlContent == expectedXml) { "XML files are not identical" }
    }


    @Test
    fun testFailToAddChild() {


        val entity = XMLEntity("fuc", "Texto existente")
        entity.addAttribute("codigo", "M4310")

        val exception = assertThrows<Exception> {
            entity.addChild(XMLEntity("nome", "Programação Avançada"))
        }

        assert(exception.message == "Cannot add a child because the entity already contains text.")
    }
    @Test
    fun testFailToAddEntityWithEspecialCara() {
        val xmlDocument = XMLDocument("root")

        val entity = XMLEntity("fuc@", "")
        entity.addAttribute("codigo", "M4310")

        val exception = assertThrows<Exception> {
            xmlDocument.addEntity(entity)
        }

        assert(exception.message == "The name of the entity must contain only letters.")
    }

   @Test
    fun testAnnotationReverseFunction(){
        val f = ReverseName("M4310", "Programação Avançada", 6.0, "xxxx")

        val xmlEntity = createXMLEntityFromClass(f)
        val outputFile = "testAnnotationReverseFunction_output.xml"
        val xmlDocument = XMLDocument("root")
        xmlDocument.addEntity(xmlEntity)
        xmlDocument.createXMLFile("TestResults",outputFile)

        val outputXmlContent = xmlDocument.normalizeXMLFile(File("TestResults/$outputFile").readText())

        val expectedXml = xmlDocument.normalizeXMLFile("""
            <?xml version = "1.0" encoding = "UTF-8"?>
            <root>
                <fuc codigo="M4310">
                    <nome>Programação Avançada</nome>
                    <stce>6.0</stce>
                </fuc>
            </root>
        """.trimIndent())

        assert(outputXmlContent == expectedXml) { "XML files are not identical" }
    }

    @Test
    fun testAnnotationChangeNames(){
        val c1 = ComponenteAvaliacao("Quizzes", 20)
        val c2 = ComponenteAvaliacao("Projeto", 80)

        val c3 = ComponenteAvaliacao("Dissertação", 60)
        val c4 = ComponenteAvaliacao("Apresentação", 20)
        val c5 = ComponenteAvaliacao("Discussão", 20)

        val f1 = FucCompoment("M4310", "Programação Avançada", 6.0, "xxxx", listOf(c1, c2))
        val f2 = FucCompoment("03782", "Dissertação", 42.0, "xxxx", listOf(c3, c4,c5))


        val exemploAula = ExemploEnunciado("Mestrado em Engenharia Informática", listOf(f1,f2),"xxxx")

        val xmlEntity = createXMLEntityFromClass(exemploAula)


        val outputFile = "testAnnotationChangeNames_output.xml"

        val xmlDocument = XMLDocument("root")

        xmlDocument.addEntity(xmlEntity)

        xmlDocument.createXMLFile("TestResults",outputFile)

        val outputXmlContent = xmlDocument.normalizeXMLFile(File("TestResults/$outputFile").readText())

        val expectedXml = xmlDocument.normalizeXMLFile("""
            <?xml version = "1.0" encoding = "UTF-8"?>
            <root>
                <plano>
                    <curso>Mestrado em Engenharia Informática</curso>
                    <mestrado>
                        <fuc codigo="M4310">
                            <nome>Programação Avançada</nome>
                            <avaliacao>
                                <componente nome="Quizzes" peso="20%"/>
                                <componente nome="Projeto" peso="80%"/>
                            </avaliacao>
                        </fuc>
                        <fuc codigo="03782">
                            <nome>Dissertação</nome>
                            <avaliacao>
                                <componente nome="Dissertação" peso="60%"/>
                                <componente nome="Apresentação" peso="20%"/>
                                <componente nome="Discussão" peso="20%"/>
                            </avaliacao>
                        </fuc>
                    </mestrado>
                </plano>
            </root>
        """.trimIndent())
        assert(outputXmlContent == expectedXml) { "XML files are not identical" }
    }



}