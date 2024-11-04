export type SupportedLanguage = {
    code: SupportedLanguageCode,
    description: string
}

export enum SupportedLanguageCode {
    EN = "en",
    DE = "de",
    RU = "ru"
}

export const supportedLanguages: SupportedLanguage[] = [
    {
        code: SupportedLanguageCode.EN,
        description: "English"
    },
    {
        code: SupportedLanguageCode.DE,
        description: "German"
    },
    {
        code: SupportedLanguageCode.RU,
        description: "Russian"
    }
];
