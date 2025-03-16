package com.tanh.scribblegame.domain.use_case.uc

import com.tanh.scribblegame.data.mappers.toPath
import com.tanh.scribblegame.data.resources.Resources
import com.tanh.scribblegame.domain.model.Path
import com.tanh.scribblegame.domain.repository.MatchRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ObservePaths @Inject constructor (
    private val matchRepository: MatchRepository
) {

    operator fun invoke(matchId: String): Flow<Resources<List<Path>, Exception>> {
        return matchRepository.observePaths(matchId).map { resource ->
            when(resource) {
                is Resources.Error -> Resources.Error(resource.error)
                is Resources.Success -> Resources.Success(resource.data.map { it.toPath() })
            }
        }
    }

}