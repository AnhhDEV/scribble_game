package com.tanh.scribblegame.domain.use_case.use_case_manager

import com.tanh.scribblegame.domain.use_case.uc.ClearPaths
import com.tanh.scribblegame.domain.use_case.uc.ObservePaths
import com.tanh.scribblegame.domain.use_case.uc.UpdateNewPath
import javax.inject.Inject

class PathManager @Inject constructor(
    val observePaths: ObservePaths,
    val updateNewPath: UpdateNewPath,
    val clearPaths: ClearPaths
)