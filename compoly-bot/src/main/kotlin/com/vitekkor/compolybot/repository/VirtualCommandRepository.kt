package com.vitekkor.compolybot.repository

import com.vitekkor.compolybot.model.VirtualCommand
import org.springframework.data.mongodb.repository.MongoRepository

interface VirtualCommandRepository : MongoRepository<VirtualCommand, String>
