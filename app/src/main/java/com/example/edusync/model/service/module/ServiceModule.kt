package com.example.edusync.model.service.module

import com.example.edusync.model.service.AccountService
import com.example.edusync.model.service.LogService
import com.example.edusync.model.service.StorageService
import com.example.edusync.model.service.impl.AccountServiceImpl
import com.example.edusync.model.service.impl.LogServiceImpl
import com.example.edusync.model.service.impl.StorageServiceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class ServiceModule {
    @Binds abstract fun provideAccountService(impl: AccountServiceImpl): AccountService

    @Binds abstract fun provideStorageService(impl: StorageServiceImpl): StorageService

    @Binds abstract fun provideLogService(impl: LogServiceImpl): LogService
}