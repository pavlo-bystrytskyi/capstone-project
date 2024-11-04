type RegistryType = {
    code: RegistryTypeCode,
    description: string
}

export enum RegistryTypeCode {
    GUEST = "guest",
    CUSTOMER = "customer"
}

export const registryTypes: RegistryType[] = [
    {
        code: RegistryTypeCode.GUEST,
        description: "create_as_guest"
    },
    {
        code: RegistryTypeCode.CUSTOMER,
        description: "create_as_customer"
    }
]

export default RegistryType;