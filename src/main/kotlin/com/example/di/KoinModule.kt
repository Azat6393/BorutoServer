package com.example.di

import com.example.repo.HeroRepository
import com.example.repo.HeroRepositoryImpl
import org.koin.dsl.module

val koinModule = module {
    single<HeroRepository>{
        HeroRepositoryImpl()
    }
}