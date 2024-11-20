import {createContext} from 'react';
import AuthContextType from "./AuthContextType.tsx";

export const AuthContext = createContext<AuthContextType | undefined>(undefined);
