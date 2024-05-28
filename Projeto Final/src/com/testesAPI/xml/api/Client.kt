package com.testesAPI.xml.api

import com.diogovaz.xml.api.*

class AddPercentage : StringTransformer {
    override fun transform(value: String): String = "$value%"
}

class FUCAdapter : XMLAdapterinter {
    override fun adapt(entity: XMLEntity): XMLEntity {
        entity.removeChildByName("ects")
        return entity
    }
}

class  ReverseAdapt : XMLAdapterinter {
    override  fun adapt(entity: XMLEntity): XMLEntity {
        val entityName = "ects"
        entity.renameChild(entityName,entityName.reversed())
        return  entity
    }
}


@XmlElement("componente")
data class ComponenteAvaliacao(
    @XmlAttribute
    val nome: String,
    @XmlAttribute
    @XmlString(AddPercentage::class)
    val peso: Int
)



@XmlAdapter(FUCAdapter::class)
@XmlElement("fuc")
data class FUC(
    @XmlAttribute val codigo: String,
    @XmlElement
    val nome: String,
    @XmlElement
    val ects: Double,
    @XmlExclude
    val observacoes: String,

    )


@XmlAdapter(FUCAdapter::class)
@XmlElement("fuc")
data class FucCompoment(
    @XmlAttribute val codigo: String,
    @XmlElement
    val nome: String,
    @XmlElement
    val ects: Double,
    @XmlExclude
    val observacoes: String,
    @XmlElement
    val avaliacao: List<ComponenteAvaliacao>,


)


@XmlAdapter(ReverseAdapt::class)
@XmlElement("fuc")
data class ReverseName(
    @XmlAttribute val codigo: String,
    @XmlElement
    val nome: String,
    @XmlElement
    val ects: Double,
    @XmlExclude
    val observacoes: String,
)



@XmlElement("plano")
data class ExemploEnunciado(
    @XmlElement val curso: String,
    @XmlElement
    val mestrado: List<FucCompoment>,
    @XmlExclude
    val observacoes: String,
)