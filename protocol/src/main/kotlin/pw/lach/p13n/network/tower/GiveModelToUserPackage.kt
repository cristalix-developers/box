package pw.lach.p13n.network.tower

import ru.cristalix.core.network.CorePackage
import java.util.*

data class GiveModelToUserPackage(
    var user: UUID,
    var model: UUID,
) : CorePackage()