package model

import eu.timepit.refined.api.Refined
import eu.timepit.refined.string.MatchesRegex
import sttp.tapir.Validator.Pattern

package object types {
    type UrlString = String
}
