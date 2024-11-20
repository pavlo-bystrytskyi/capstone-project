import {createContext} from "react";
import ToastContextType from "./ToastContextType.tsx";

export const ToastContext = createContext<ToastContextType | undefined>(undefined);
