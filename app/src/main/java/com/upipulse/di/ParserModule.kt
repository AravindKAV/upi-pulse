package com.upipulse.di

import com.upipulse.ingestion.parser.RegexUpiParser
import com.upipulse.ingestion.parser.TransactionPayloadParser
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet

@Module
@InstallIn(SingletonComponent::class)
interface ParserModule {
    @Binds
    @IntoSet
    fun bindRegexParser(parser: RegexUpiParser): TransactionPayloadParser
}