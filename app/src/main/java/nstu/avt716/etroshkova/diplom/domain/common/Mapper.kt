package nstu.avt716.etroshkova.diplom.domain.common

open class Mapper<Business, Dto>(
    private val fromBusinessMapper: (Business) -> Dto = unsupported(
        "BO -> DTO is unsupported"
    ),
    private val fromDtoMapper: (Dto) -> Business = unsupported(
        "DTO -> BO is unsupported"
    )
) {

    private companion object {
        fun unsupported(message: String): (Any?) -> Nothing =
            { throw UnsupportedOperationException(message) }
    }

    fun fromBusiness(business: Business) = business.let(fromBusinessMapper)

    fun fromDto(dto: Dto) = dto.let(fromDtoMapper)
}
