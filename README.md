# GACAndroidArchitecture
A powerful android rx-active Architecture merged with Google Architecture Components tools

## 1. A Brief Introduciton
The app is a sample project that shows how to implement the GACAndroidArchitecture into your Android app.

### 1.1 What is GACAndroidArchitecture?
It is a layer-based architecture that allows a real disentangle of the UI components from the business logic. 

![alt text](https://cdn-images-1.medium.com/max/800/1*I9WPcnpGNuI4CjxxrkP0-g.png "Simple Architecture Diagram")

The main components are:

* UI
* UIModel
* ViewModels with Livedata
* UseCase
* Repository

### 1.2 GACAndroidArchitecture main components

#### 1.2.1 UI

The UI layer of the architecture comprises activities, fragments and views. 
The role of an activity is to coordinate the navigations by managing widgets and screens. 

Fragments are instead the components that request and use the coordinators requesting them to the Coordinator Manager. Each fragment can use multiple coordinators and one coodinators can be used by several fragments.
The communications among them take place by means of an EventDispatcher.

#### 1.2.2 UIModel

WIP

#### 1.2.3 ViewModels with Livedata

WIP

#### 1.2.4 UseCase
A **UseCase** is a wrapper for a small business logic operation. A **UseCase** can use one or more **Repository** to get or write the requested data, then it returns the response event.

#### 1.2.5 Repository
A **Repository** handles the process of saving or retrieving data from a datasource, it is managed by one or more **UseCase**.

## 2. GACAndroidArchitecture Classes Overview

WIP

## 3. How to use it?

WIP

# Licence

      Copyright (C) 2017 Sysdata S.p.A.

      Licensed under the Apache License, Version 2.0 (the "License");
      you may not use this file except in compliance with the License.
      You may obtain a copy of the License at

          http://www.apache.org/licenses/LICENSE-2.0

      Unless required by applicable law or agreed to in writing, software
      distributed under the License is distributed on an "AS IS" BASIS,
      WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
      See the License for the specific language governing permissions and
      limitations under the License.
 
